package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.ManualProperties.DefineConfig;
import com.x.processplatform.core.entity.element.ManualProperties.GoBackConfig;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;

class V2ListActivityGoBack extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2ListActivityGoBack.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, id:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> id,
				() -> jsonElement);

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		ActionResult<List<Wo>> result = new ActionResult<>();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			Work work = emc.find(id, Work.class);

			if (null == work) {
				throw new ExceptionEntityExist(id, Work.class);
			}

			Business business = new Business(emc);

			if (!business.editable(effectivePerson, work)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			Manual manual = (Manual) business.getActivity(work.getActivity(), ActivityType.manual);

			if (null == manual) {
				throw new ExceptionEntityExist(work.getActivity());
			}

			// 1.允许goBack,2.多待办允许goBack或待办只有一条
			if (BooleanUtils.isTrue(manual.getAllowGoBack())
					&& (BooleanUtils.isNotFalse(manual.getGoBackConfig().getMultiTaskEnable())
							|| emc.countEqualAndEqual(Task.class, Task.activityToken_FIELDNAME, work.getActivityToken(),
									Task.job_FIELDNAME, work.getJob()) <= 1)) {
				WorkLogTree workLogTree = this.workLogTree(business, work.getJob());
				Node node = workLogTree.location(work);
				if (null != node) {
					List<WorkLog> workLogs = new ArrayList<>();
					if (BooleanUtils.isNotFalse(wi.getLastOnly())) {
						// 过滤掉未链接的,过滤掉不是manual活动的,每个活动只取最近一次的workLog
						workLogs = workLogTree.up(node).stream().map(Node::getWorkLog)
								.filter(o -> Objects.equals(o.getArrivedActivityType(), ActivityType.manual)
										&& BooleanUtils.isTrue(o.getConnected()))
								.collect(Collectors.groupingBy(WorkLog::getFromActivity)).entrySet().stream()
								.map(o -> o.getValue().get(0)).collect(Collectors.toList());
					} else {
						workLogs = workLogTree.up(node).stream().map(Node::getWorkLog)
								.filter(o -> Objects.equals(o.getArrivedActivityType(), ActivityType.manual)
										&& BooleanUtils.isTrue(o.getConnected()))
								.collect(Collectors.toList());
					}
					List<Wo> wos = this.list(manual, workLogs);
					wos = this.supplement(business, manual, wos);
				}
			}

		}
		return result;

	}

	private List<Wo> list(Manual manual, List<WorkLog> workLogs) {
		List<Wo> list = new ArrayList<>();
		if (StringUtils.equalsIgnoreCase(manual.getGoBackConfig().getType(), GoBackConfig.TYPE_PREV)) {
			Optional<WorkLog> opt = workLogs.stream()
					.filter(o -> Objects.equals(o.getFromActivityType(), ActivityType.manual)
							&& BooleanUtils.isTrue((o.getConnected())))
					.findFirst();
			if (opt.isPresent()) {
				Wo wo = new Wo();
				wo.setActivity(opt.get().getFromActivity());
				wo.setLastUpdateTime(opt.get().getFromTime());
				wo.setActivityToken(opt.get().getFromActivityToken());
				wo.setWay(manual.getGoBackConfig().getWay());
				list.add(wo);
			}
		} else if (StringUtils.equalsIgnoreCase(manual.getGoBackConfig().getType(), GoBackConfig.TYPE_DEFINE)) {
			workLogs.stream().forEach(o -> {
				Optional<DefineConfig> opt = manual.getGoBackConfig().getDefineConfigList().stream()
						.filter(d -> StringUtils.equalsIgnoreCase(d.getActivity(), o.getFromActivity())).findFirst();
				if (opt.isPresent()) {
					Wo wo = new Wo();
					wo.setActivity(opt.get().getActivity());
					wo.setWay(opt.get().getWay());
					wo.setLastUpdateTime(o.getFromTime());
					wo.setActivityToken(o.getFromActivityToken());
					list.add(wo);
				}
			});
		} else {
			workLogs.stream().forEach(o -> {
				Wo wo = new Wo();
				wo.setActivity(o.getFromActivity());
				wo.setLastUpdateTime(o.getFromTime());
				wo.setActivityToken(o.getFromActivityToken());
				wo.setWay(manual.getGoBackConfig().getWay());
				list.add(wo);
			});
		}
		return list;
	}

	private List<Wo> supplement(Business business, Manual manual, List<Wo> wos) {
		List<Wo> list = new ArrayList<>();
		wos.stream().forEach(o -> {
			try {
				Manual m = (Manual) business.getActivity(o.getActivity(), ActivityType.manual);
				if (null != m) {
					o.setName(manual.getName());
					list.add(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
		// 拼装上最后一次环节处理人和处理时间
		list.stream().forEach(o -> {
			try {
				o.setLastIdentityList(business.entityManagerContainer()
						.fetchEqualAndEqual(TaskCompleted.class, Arrays.asList(TaskCompleted.identity_FIELDNAME),
								TaskCompleted.activityToken_FIELDNAME, o.getActivityToken(),
								TaskCompleted.joinInquire_FIELDNAME, true)
						.stream().map(TaskCompleted::getIdentity).collect(Collectors.toList()));
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
		// 过滤掉为空不可goBack的节点
		return list.stream().filter(o -> ListTools.isNotEmpty(o.getLastIdentityList())).collect(Collectors.toList());
	}

	private WorkLogTree workLogTree(Business business, String job) throws Exception {
		return new WorkLogTree(business.entityManagerContainer().fetchEqual(WorkLog.class,
				WorkLogTree.RELY_WORKLOG_ITEMS, WorkLog.JOB_FIELDNAME, job));
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -7690519507479509393L;

		// 上次处理时间
		private Date lastUpdateTime;
		// 返回方式
		private String way;
		// 返回方式
		private String activityToken;
		// 活动标识
		private String activity;
		// 活动名称
		private String name;
		// 上次处理人
		private List<String> lastIdentityList = new ArrayList<>();

		public String getActivityToken() {
			return activityToken;
		}

		public void setActivityToken(String activityToken) {
			this.activityToken = activityToken;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getActivity() {
			return activity;
		}

		public void setActivity(String activity) {
			this.activity = activity;
		}

		public String getWay() {
			return way;
		}

		public void setWay(String way) {
			this.way = way;
		}

		public Date getLastUpdateTime() {
			return lastUpdateTime;
		}

		public void setLastUpdateTime(Date lastUpdateTime) {
			this.lastUpdateTime = lastUpdateTime;
		}

		public List<String> getLastIdentityList() {
			return lastIdentityList;
		}

		public void setLastIdentityList(List<String> lastIdentityList) {
			this.lastIdentityList = lastIdentityList;
		}

	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -6178473080370914961L;

		// 重复经过的活动仅取最近的,默认为true
		private Boolean lastOnly;

		public Boolean getLastOnly() {
			return lastOnly;
		}

		public void setLastOnly(Boolean lastOnly) {
			this.lastOnly = lastOnly;
		}

	}
}