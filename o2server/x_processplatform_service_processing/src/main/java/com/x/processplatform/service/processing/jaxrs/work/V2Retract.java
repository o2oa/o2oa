package com.x.processplatform.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
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
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;
import com.x.processplatform.core.entity.ticket.Tickets;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2RetractWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2RetractWo;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;
import com.x.processplatform.service.processing.processor.AeiObjects;

class V2Retract extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Retract.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> id,
				() -> jsonElement);

		Param param = this.init(id, jsonElement);

		CallableImpl callable = new CallableImpl(param);

		return ProcessPlatformKeyClassifyExecutorFactory.get(param.job).submit(callable).get(300, TimeUnit.SECONDS);

	}

	private Param init(String id, JsonElement jsonElement) throws Exception {
		Param param = new Param();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			param.id = work.getId();
			param.job = work.getJob();
			TaskCompleted taskCompleted = emc.find(wi.getTaskCompleted(), TaskCompleted.class);
			if (null == taskCompleted) {
				throw new ExceptionEntityNotExist(wi.getTaskCompleted(), TaskCompleted.class);
			}
			param.taskCompleted = taskCompleted;
			WorkLog workLog = emc.find(wi.getWorkLog(), WorkLog.class);
			if (null == workLog) {
				throw new ExceptionEntityNotExist(wi.getWorkLog(), WorkLog.class);
			}
			param.workLog = workLog;
			Manual manual = emc.find(workLog.getFromActivity(), Manual.class);
			if (null == manual) {
				throw new ExceptionEntityNotExist(workLog.getFromActivity(), Manual.class);
			}
			param.manual = manual;
		}
		return param;
	}

	private class Param {

		private String id;
		private String job;
		private WorkLog workLog;
		private TaskCompleted taskCompleted;
		private Manual manual;

	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private Param param;

		private CallableImpl(Param param) {
			this.param = param;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			WorkLogTree tree;
			WorkLog workLog;
			TaskCompleted taskCompleted;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Work work = business.entityManagerContainer().find(param.id, Work.class);
				AeiObjects aeiObjects = new AeiObjects(business, work, param.manual, new ProcessingAttributes());
				tree = new WorkLogTree(aeiObjects.getWorkLogs());
				workLog = getTargetWorkLog(aeiObjects.getWorkLogs(), param.workLog.getId());
				taskCompleted = getTaskCompleted(business, param.taskCompleted.getId());
				Node workLogNode = tree.find(workLog);
				Nodes nodes = tree.down(workLogNode);
				List<String> activityTokens = activityTokenOfNodes(nodes);

				aeiObjects.getTasks().stream().filter(o -> activityTokens.contains(o.getActivityToken())).forEach(o -> {
					// 标记应为撤回删除
					o.setRouteName("retract");
					aeiObjects.getDeleteTasks().add(o);
				});

				aeiObjects.getTaskCompleteds().stream().filter(o -> activityTokens.contains(o.getActivityToken()))
						.forEach(aeiObjects.getDeleteTaskCompleteds()::add);

				aeiObjects.getReads().stream().filter(o -> activityTokens.contains(o.getActivityToken()))
						.forEach(aeiObjects.getDeleteReads()::add);

				aeiObjects.getReadCompleteds().stream().filter(o -> activityTokens.contains(o.getActivityToken()))
						.forEach(aeiObjects.getDeleteReadCompleteds()::add);

				aeiObjects.getRecords().stream().filter(o -> activityTokens.contains(o.getFromActivityToken()))
						.forEach(aeiObjects.getDeleteRecords()::add);

				aeiObjects.getWorkLogs().stream().filter(o -> activityTokens.contains(o.getFromActivityToken()))
						.forEach(aeiObjects.getDeleteWorkLogs()::add);

				List<String> workIds = workOfNodes(nodes);

				workIds = ListUtils.subtract(workIds, ListTools.toList(work.getId()));

				aeiObjects.getDeleteWorks().addAll(business.entityManagerContainer()
						.listEqualAndIn(Work.class, Work.job_FIELDNAME, param.job, JpaObject.id_FIELDNAME, workIds)
						.stream().filter(o -> !Objects.equals(o, work)).collect(Collectors.toList()));

				if (StringUtils.isNotEmpty(param.manual.getForm())) {
					Form form = business.element().get(param.manual.getForm(), Form.class);
					if (null != form) {
						work.setForm(param.manual.getForm());
					}
				}

				update(work, workLog, param.manual);

				// 必然不为null
				taskCompleted.setProcessingType(TaskCompleted.PROCESSINGTYPE_RETRACT);
				taskCompleted.setJoinInquire(false);
				aeiObjects.getUpdateTaskCompleteds().add(taskCompleted);
				Tickets tickets = param.manual.identitiesToTickets(Stream
						.concat(Stream.of(taskCompleted.getDistinguishedName()), Stream.of(taskCompleted.getIdentity()))
						.filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList()));
				work.setTickets(tickets);
				aeiObjects.getUpdateWorks().add(work);
				aeiObjects.commit();
			}

			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}

		private TaskCompleted getTaskCompleted(Business business, String taskCompletedId) throws Exception {
			TaskCompleted taskCompleted = business.entityManagerContainer().find(taskCompletedId, TaskCompleted.class);
			if (null == taskCompleted) {
				throw new ExceptionEntityNotExist(taskCompletedId, TaskCompleted.class);
			}
			return taskCompleted;
		}

		private void update(Work work, WorkLog workLog, Activity activity) {
			work.setActivity(activity.getId());
			work.setActivityAlias(activity.getAlias());
			work.setActivityName(activity.getName());
			work.setActivityDescription(activity.getDescription());
			work.setActivityToken(workLog.getFromActivityToken());
			work.setSplitting(workLog.getSplitting());
			work.setSplitToken(workLog.getSplitToken());
			work.setSplitValue(workLog.getSplitValue());
			workLog.setConnected(false);
		}

		private WorkLog getTargetWorkLog(List<WorkLog> list, String id) throws ExceptionEntityNotExist {
			WorkLog workLog = list.stream().filter(o -> StringUtils.equals(o.getId(), id)).findFirst().orElse(null);
			if (null == workLog) {
				throw new ExceptionEntityNotExist(id, WorkLog.class);
			}
			return workLog;
		}

		private List<String> activityTokenOfNodes(Nodes nodes) {
			List<String> list = new ArrayList<>();
			for (Node o : nodes) {
				list.add(o.getWorkLog().getFromActivityToken());
			}
			return ListTools.trim(list, true, true);
		}

		private List<String> workOfNodes(Nodes nodes) {
			List<String> list = new ArrayList<>();
			for (Node o : nodes) {
				list.add(o.getWorkLog().getWork());
			}
			return ListTools.trim(list, true, true);
		}
	}

	public static class Wi extends V2RetractWi {

		private static final long serialVersionUID = 2124526379618584365L;

	}

	public static class Wo extends V2RetractWo {

		private static final long serialVersionUID = -1571428251733726998L;
	}
}
