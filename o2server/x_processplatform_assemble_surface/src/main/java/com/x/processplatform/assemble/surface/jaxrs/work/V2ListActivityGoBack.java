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
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;

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

			WorkLogTree workLogTree = this.workLogTree(business, work.getJob());

			Node node = workLogTree.location(work);

			if (null != node) {
				result.setData(list(business, manual, workLogTree, node));
			}
		}
		return result;
	}

	private List<Wo> list(Business business, Manual manual, WorkLogTree workLogTree, Node node) {
		final Nodes nodes = workLogTree.up(node);
		List<Wo> list = new ArrayList<>();
		nodes.stream()
				.filter(o -> Objects.equals(o.getWorkLog().getArrivedActivityType(), ActivityType.manual)
						&& BooleanUtils.isTrue(o.getWorkLog().getConnected()))
				.map(o -> o.getWorkLog().getFromActivity()).distinct().forEach(o -> {
					try {
						Manual m = (Manual) business.getActivity(o, ActivityType.manual);
						if (null != m) {
							Wo wo = new Wo();
							wo.setName(manual.getName());
							wo.setId(manual.getId());
							list.add(wo);
						}
					} catch (Exception e) {
						LOGGER.error(e);
					}
				});
		// 拼装上最后一次环节处理人和处理时间
		list.stream().forEach(o -> {
			Optional<Node> opt = nodes.stream()
					.filter(n -> StringUtils.equalsIgnoreCase(n.getWorkLog().getFromActivity(), o.getId())).findFirst();
			if (opt.isPresent()) {
				try {
					o.setLastIdentityList(business.entityManagerContainer()
							.fetchEqualAndEqual(TaskCompleted.class, Arrays.asList(TaskCompleted.identity_FIELDNAME),
									TaskCompleted.activityToken_FIELDNAME,
									opt.get().getWorkLog().getFromActivityToken(), TaskCompleted.joinInquire_FIELDNAME,
									true)
							.stream().map(TaskCompleted::getIdentity).collect(Collectors.toList()));
					o.setLastUpdateTime(opt.get().getWorkLog().getFromTime());
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
		});
		// 过滤掉为空不可goBack的节点
		return list.stream()
				.filter(o -> (!Objects.isNull(o.getLastUpdateTime())) && ListTools.isNotEmpty(o.getLastIdentityList()))
				.collect(Collectors.toList());
	}

	private WorkLogTree workLogTree(Business business, String job) throws Exception {
		return new WorkLogTree(business.entityManagerContainer().fetchEqual(WorkLog.class,
				WorkLogTree.RELY_WORKLOG_ITEMS, WorkLog.JOB_FIELDNAME, job));
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -7690519507479509393L;

		// 活动名称
		private String name;

		// 活动标识
		private String id;

		// 上次处理时间
		private Date lastUpdateTime;

		// 上次处理人
		private List<String> lastIdentityList = new ArrayList<>();

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
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

		private Boolean all;

	}
}