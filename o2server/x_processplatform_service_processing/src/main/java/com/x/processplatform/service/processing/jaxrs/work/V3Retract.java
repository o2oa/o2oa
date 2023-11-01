package com.x.processplatform.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2RetractWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2RetractWo;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class V3Retract extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V3Retract.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> id,
				() -> jsonElement);

		Param param = this.init(id, jsonElement);

		return ProcessPlatformKeyClassifyExecutorFactory.get(param.job).submit(new CallableImpl(param)).get(300,
				TimeUnit.SECONDS);

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
			param.taskCompleted = wi.getTaskCompleted();
			param.workLog = wi.getWorkLog();
		}
		return param;
	}

	public static class CallableImpl implements Callable<ActionResult<Wo>> {

		private Param param;

		private CallableImpl(Param param) {
			this.param = param;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			Work work;
			WorkLogTree tree;
			WorkLog workLog;
			TaskCompleted taskCompleted;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				work = getWork(business, param.id);
				List<WorkLog> workLogs = emc.listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, work.getJob());
				tree = new WorkLogTree(emc.listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, work.getJob()));
				workLog = getTargetWorkLog(workLogs, param.workLog);
				taskCompleted = getTaskCompleted(business, param.taskCompleted);
				Node workLogNode = tree.find(workLog);
				Nodes nodes = tree.down(workLogNode);
				List<String> activityTokens = activityTokenOfNodes(nodes);
				emc.beginTransaction(Task.class);
				emc.beginTransaction(TaskCompleted.class);
				emc.beginTransaction(Read.class);
				emc.beginTransaction(ReadCompleted.class);
				// emc.beginTransaction(WorkLog.class);
				emc.beginTransaction(Work.class);
				emc.beginTransaction(Record.class);
				List<Task> removeTasks = deleteTasks(business, work.getJob(), activityTokens);
				List<TaskCompleted> removeTaskCompleteds = deleteTaskCompleteds(business, work.getJob(),
						activityTokens);
				List<Read> removeReads = deleteReads(business, work.getJob(), activityTokens);
				List<ReadCompleted> removeReadCompleteds = deleteReadCompleteds(business, work.getJob(),
						activityTokens);
				deleteRecords(business, work.getJob(), activityTokens);
				// deleteWorkLogs(business, work.getJob(), activityTokens);

				List<String> workIds = workOfNodes(nodes);

				workIds = ListUtils.subtract(workIds, ListTools.toList(work.getId()));

				deleteWorks(business, work.getJob(), workIds);

				Manual manual = (Manual) business.element().get(workLog.getFromActivity(), ActivityType.manual);

				if (null == manual) {
					throw new ExceptionEntityNotExist(workLog.getFromActivity());
				}

				// 替换表单
				if (StringUtils.isNotEmpty(manual.getForm())) {
					Form form = business.element().get(manual.getForm(), Form.class);
					if (null != form) {
						work.setForm(manual.getForm());
					}
				}

				// 必然不为null
				taskCompleted.setProcessingType(TaskCompleted.PROCESSINGTYPE_RETRACT);
				taskCompleted.setJoinInquire(false);
				List<String> manualTaskIdentityList = new ArrayList<>();
				manualTaskIdentityList.add(taskCompleted.getIdentity());
				work.setTickets(manual.identitiesToTickets(Arrays.asList(taskCompleted.getDistinguishedName())));
				work.setDestinationActivity(manual.getId());
				work.setDestinationActivityType(manual.getActivityType());
				// 发送消息
				sendRemoveMessages(removeTasks, removeTaskCompleteds, removeReads, removeReadCompleteds);
				emc.commit();
			}

			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}

		private Work getWork(Business business, String workId) throws Exception {
			Work work = business.entityManagerContainer().find(workId, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			Application application = business.element().get(work.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionEntityNotExist(work.getApplication(), Application.class);
			}
			Process process = business.element().get(work.getProcess(), Process.class);
			if (null == process) {
				throw new ExceptionEntityNotExist(work.getProcess(), Process.class);
			}
			return work;
		}

		private WorkLog getTargetWorkLog(List<WorkLog> list, String id) throws ExceptionEntityNotExist {
			WorkLog workLog = list.stream().filter(o -> StringUtils.equals(o.getId(), id)).findFirst().orElse(null);
			if (null == workLog) {
				throw new ExceptionEntityNotExist(id, WorkLog.class);
			}
			return workLog;
		}

		private TaskCompleted getTaskCompleted(Business business, String taskCompletedId) throws Exception {
			TaskCompleted taskCompleted = business.entityManagerContainer().find(taskCompletedId, TaskCompleted.class);
			if (null == taskCompleted) {
				throw new ExceptionEntityNotExist(taskCompletedId, TaskCompleted.class);
			}
			return taskCompleted;
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

		private List<Task> deleteTasks(Business business, String job, List<String> activityTokens) throws Exception {
			List<Task> os = business.entityManagerContainer().listEqualAndIn(Task.class, Task.job_FIELDNAME, job,
					Task.activityToken_FIELDNAME, activityTokens);
			for (Task o : os) {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
			}
			return os;
		}

		private List<TaskCompleted> deleteTaskCompleteds(Business business, String job, List<String> activityTokens)
				throws Exception {
			List<TaskCompleted> os = business.entityManagerContainer().listEqualAndIn(TaskCompleted.class,
					TaskCompleted.job_FIELDNAME, job, TaskCompleted.activityToken_FIELDNAME, activityTokens);
			for (TaskCompleted o : os) {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
			}
			return os;
		}

		private List<Read> deleteReads(Business business, String job, List<String> activityTokens) throws Exception {
			List<Read> os = business.entityManagerContainer().listEqualAndIn(Read.class, Read.job_FIELDNAME, job,
					Read.activityToken_FIELDNAME, activityTokens);
			for (Read o : os) {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
			}
			return os;
		}

		private List<ReadCompleted> deleteReadCompleteds(Business business, String job, List<String> activityTokens)
				throws Exception {
			List<ReadCompleted> os = business.entityManagerContainer().listEqualAndIn(ReadCompleted.class,
					ReadCompleted.job_FIELDNAME, job, ReadCompleted.activityToken_FIELDNAME, activityTokens);
			for (ReadCompleted o : os) {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
			}
			return os;
		}

		private void deleteRecords(Business business, String job, List<String> activityTokens) throws Exception {
			List<Record> os = business.entityManagerContainer().listEqualAndIn(Record.class, Record.job_FIELDNAME, job,
					Record.fromActivityToken_FIELDNAME, activityTokens);
			for (Record o : os) {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
			}
		}

		private void deleteWorkLogs(Business business, String job, List<String> activityTokens) throws Exception {
			List<WorkLog> os = business.entityManagerContainer().listEqualAndIn(WorkLog.class, WorkLog.JOB_FIELDNAME,
					job, WorkLog.FROMACTIVITYTOKEN_FIELDNAME, activityTokens);
			for (WorkLog o : os) {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
			}
		}

		private void deleteWorks(Business business, String job, List<String> workIds) throws Exception {
			List<Work> os = business.entityManagerContainer().listEqualAndIn(Work.class, Work.job_FIELDNAME, job,
					JpaObject.id_FIELDNAME, workIds);
			for (Work o : os) {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
			}
		}

		private void sendRemoveMessages(List<Task> removeTasks, List<TaskCompleted> removeTaskCompleteds,
				List<Read> removeReads, List<ReadCompleted> removeReadCompleteds) throws Exception {
			for (Task o : removeTasks) {
				MessageFactory.task_delete(o);
			}
			for (TaskCompleted o : removeTaskCompleteds) {
				MessageFactory.taskCompleted_delete(o);
			}
			for (Read o : removeReads) {
				MessageFactory.read_delete(o);
			}
			for (ReadCompleted o : removeReadCompleteds) {
				MessageFactory.readCompleted_delete(o);
			}
		}
	}

	public static class Param {

		private String id;
		private String job;
		private String workLog;
		private String taskCompleted;

	}

	public static class Wi extends V2RetractWi {

		private static final long serialVersionUID = 2124526379618584365L;

	}

	public static class Wo extends V2RetractWo {

		private static final long serialVersionUID = -1571428251733726998L;

	}
}
