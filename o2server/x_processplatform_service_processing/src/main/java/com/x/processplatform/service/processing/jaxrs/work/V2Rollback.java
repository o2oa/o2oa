package com.x.processplatform.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;
import com.x.processplatform.core.entity.ticket.Tickets;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2RollbackWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2RollbackWo;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;
import com.x.processplatform.service.processing.processor.AeiObjects;
import com.x.processplatform.service.processing.processor.TaskTickets;

class V2Rollback extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Rollback.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		Param param = this.init(id, jsonElement);

		CallableImpl callable = new CallableImpl(param);

		return ProcessPlatformKeyClassifyExecutorFactory.get(param.job).submit(callable).get(300, TimeUnit.SECONDS);

	}

	private Param init(String id, JsonElement jsonElement) throws Exception {
		Param param = new Param();
		final Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Work work = emc.fetch(id, Work.class,
					ListTools.toList(Work.job_FIELDNAME, Work.application_FIELDNAME, Work.process_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			Application application = business.element().get(work.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionEntityNotExist(work.getApplication(), Application.class);
			}
			Process process = business.element().get(work.getProcess(), Process.class);
			if (null == process) {
				throw new ExceptionEntityNotExist(work.getProcess(), Process.class);
			}
			param.job = work.getJob();
			param.work = work;
			WorkLog workLog = emc.find(wi.getWorkLog(), WorkLog.class);
			if (null == workLog) {
				throw new ExceptionEntityNotExist(wi.getWorkLog(), WorkLog.class);
			}
			param.workLog = workLog;
			param.distinguishedNameList = wi.getDistinguishedNameList();
		}
		return param;
	}

	private class Param {

		private String job;
		private Work work;
		private WorkLog workLog;
		private List<String> distinguishedNameList;

	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private Param param;

		private CallableImpl(Param param) {
			this.param = param;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Work work = emc.find(param.work.getId(), Work.class);
				List<WorkLog> workLogs = emc.listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, work.getJob());
				WorkLogTree tree = new WorkLogTree(emc.listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, work.getJob()));
				WorkLog workLog = getTargetWorkLog(workLogs, param.workLog.getId());
				Activity activity = business.element().getActivity(workLog.getFromActivity());
				AeiObjects aeiObjects = new AeiObjects(business, work, activity, new ProcessingAttributes());
				Node workLogNode = tree.find(workLog);
				Nodes nodes = tree.down(workLogNode);
				List<String> activityTokens = activityTokenOfNodes(nodes);

				LOGGER.debug("activityTokens:{}.", () -> gson.toJson(activityTokens));

				aeiObjects.getTasks().stream().filter(o -> activityTokens.contains(o.getActivityToken()))
						.forEach(aeiObjects.getDeleteTasks()::add);

				// 将已有的已办标识为joinInquire=false,这样由于存在已办所以撤回将被禁用.
				aeiObjects.getTaskCompleteds().stream()
						.filter(o -> StringUtils.equals(workLog.getFromActivityToken(), o.getActivityToken()))
						.forEach(o -> {
							o.setJoinInquire(false);
							aeiObjects.getUpdateTaskCompleteds().add(o);
						});

				aeiObjects.getTaskCompleteds().stream().filter(o -> activityTokens.contains(o.getActivityToken()))
						// 前面已经更新,不能再次删除
						.filter(o -> !aeiObjects.getUpdateTaskCompleteds().contains(o))
						.forEach(aeiObjects.getDeleteTaskCompleteds()::add);

				// 调整latest标识,否则有不是latest的已办不会在已办列表中显示
				aeiObjects.getTaskCompleteds().stream().filter(o -> !aeiObjects.getDeleteTaskCompleteds().contains(o))
						.collect(Collectors.groupingBy(TaskCompleted::getPerson)).entrySet().stream()
						.map(Map.Entry::getValue)
						.filter(o -> o.stream().noneMatch(t -> BooleanUtils.isTrue(t.getLatest()))).forEach(o -> {
							Optional<TaskCompleted> opt = o.stream()
									.sorted(Comparator.comparing(TaskCompleted::getCompletedTime,
											Comparator.nullsFirst(Date::compareTo).reversed()))
									.findFirst();
							if (opt.isPresent()) {
								opt.get().setLatest(true);
								aeiObjects.getUpdateTaskCompleteds().add(opt.get());
							}
						});

				aeiObjects.getReads().stream().filter(o -> activityTokens.contains(o.getActivityToken()))
						.forEach(aeiObjects.getDeleteReads()::add);

				aeiObjects.getReadCompleteds().stream().filter(o -> activityTokens.contains(o.getActivityToken()))
						.forEach(aeiObjects.getDeleteReadCompleteds()::add);

				aeiObjects.getRecords().stream().filter(o -> activityTokens.contains(o.getFromActivityToken()))
						.forEach(aeiObjects.getDeleteRecords()::add);

				aeiObjects.getWorkLogs().stream().filter(o -> activityTokens.contains(o.getFromActivityToken()))
						.forEach(aeiObjects.getDeleteWorkLogs()::add);

				aeiObjects.getReviews().stream()
						.filter(o -> o.getCreateTime().getTime() > workLog.getCreateTime().getTime())
						.forEach(aeiObjects.getDeleteReviews()::add);

				List<String> workIds = ListUtils.subtract(workOfNodes(nodes), ListTools.toList(work.getId()));

				aeiObjects.getWorks().stream().filter(o -> workIds.contains(o.getId()))
						.forEach(o -> aeiObjects.getDeleteWorks().add(o));

				update(business, work, workLog);

				updateTickets(business, aeiObjects, param.distinguishedNameList);
				aeiObjects.getUpdateWorks().add(work);
				aeiObjects.commit();
			}

			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			wo.setId(param.work.getId());
			result.setData(wo);
			return result;
		}

		private void update(Business business, Work work, WorkLog workLog) throws Exception {
			work.setActivity(workLog.getFromActivity());
			work.setActivityType(workLog.getFromActivityType());
			work.setActivityAlias(workLog.getFromActivityAlias());
			work.setActivityName(workLog.getFromActivityName());
			work.setActivityToken(workLog.getFromActivityToken());
			work.setSplitting(workLog.getSplitting());
			work.setSplitToken(workLog.getSplitToken());
			work.setSplitValue(workLog.getSplitValue());
			// 重新设置表单
			String formId = business.element().lookupSuitableForm(work.getProcess(), work.getActivity());
			if (StringUtils.isNotBlank(formId)) {
				work.setForm(formId);
			}
			workLog.setConnected(false);
			workLog.setArrivedActivity("");
			workLog.setArrivedActivityAlias("");
			workLog.setArrivedActivityName("");
			workLog.setArrivedActivityToken("");
			workLog.setArrivedActivityType(null);
			workLog.setArrivedGroup(null);
			workLog.setArrivedOpinionGroup(null);
			workLog.setArrivedTime(null);
		}

		private void updateTickets(Business business, AeiObjects aeiObjects, List<String> distinguishedNameList)
				throws Exception {
			if (Objects.equals(ActivityType.manual, aeiObjects.getWork().getActivityType())) {
				Manual manual = (Manual) business.element().get(aeiObjects.getWork().getActivity(),
						ActivityType.manual);
				if ((null != manual) && ListTools.isNotEmpty(distinguishedNameList)) {
					aeiObjects.getWork().setTickets(TaskTickets.translate(aeiObjects, manual, distinguishedNameList));
					return;
				}
			}
			aeiObjects.getWork().setTickets(new Tickets());
		}

		private WorkLog getTargetWorkLog(List<WorkLog> list, String id) throws ExceptionEntityNotExist {
			Optional<WorkLog> opt = list.stream().filter(o -> StringUtils.equals(o.getId(), id)).findFirst();
			if (opt.isEmpty()) {
				throw new ExceptionEntityNotExist(id, WorkLog.class);
			}
			return opt.get();
		}

		private List<String> activityTokenOfNodes(Nodes nodes) {
			List<String> list = new ArrayList<>();
			for (Node o : nodes) {
				list.add(o.getWorkLog().getFromActivityToken());
			}
			return ListTools.trim(list, true, true);
		}

		private List<String> workOfNodes(Nodes nodes) {
			List<String> os = new ArrayList<>();
			for (Node o : nodes) {
				os.add(o.getWorkLog().getWork());
			}
			return ListTools.trim(os, true, true);
		}

	}

	public static class Wi extends V2RollbackWi {

		private static final long serialVersionUID = 1549664177644024435L;

	}

	public static class Wo extends V2RollbackWo {

		private static final long serialVersionUID = 7732547960719161607L;
	}
}