package com.x.processplatform.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2RetractWi;
import com.x.processplatform.service.processing.Business;

class V2Retract extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		final Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		final String job;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			job = work.getJob();
		}

		Callable<ActionResult<Wo>> callable = new Callable<ActionResult<Wo>>() {
			public ActionResult<Wo> call() throws Exception {
				Work work;
				WorkLogTree tree;
				WorkLog workLog;
				TaskCompleted taskCompleted;
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					work = getWork(business, id);
					List<WorkLog> workLogs = emc.listEqual(WorkLog.class, WorkLog.job_FIELDNAME, work.getJob());
					tree = new WorkLogTree(emc.listEqual(WorkLog.class, WorkLog.job_FIELDNAME, work.getJob()));
					workLog = getTargetWorkLog(workLogs, wi.getWorkLog());
					taskCompleted = getTaskCompleted(business, wi.getTaskCompleted());
					Node workLogNode = tree.find(workLog);
					Nodes nodes = tree.down(workLogNode);
					List<String> activityTokens = activityTokenOfNodes(nodes);
					emc.beginTransaction(Task.class);
					emc.beginTransaction(TaskCompleted.class);
					emc.beginTransaction(Read.class);
					emc.beginTransaction(ReadCompleted.class);
					emc.beginTransaction(WorkLog.class);
					emc.beginTransaction(Work.class);
					emc.beginTransaction(Record.class);

					deleteTasks(business, work.getJob(), activityTokens);
					deleteTaskCompleteds(business, work.getJob(), activityTokens);
					deleteReads(business, work.getJob(), activityTokens);
					deleteReadCompleteds(business, work.getJob(), activityTokens);
					deleteRecords(business, work.getJob(), activityTokens);
					deleteWorkLogs(business, work.getJob(), activityTokens);

					List<String> workIds = workOfNodes(nodes);

					workIds = ListUtils.subtract(workIds, ListTools.toList(work.getId()));

					deleteWorks(business, work.getJob(), workIds);

					update(work, workLog);

					if (null != taskCompleted) {
						taskCompleted.setProcessingType(TaskCompleted.PROCESSINGTYPE_RETRACT);
						List<String> manualTaskIdentityList = new ArrayList<>();
						manualTaskIdentityList.add(taskCompleted.getIdentity());
						work.setManualTaskIdentityList(manualTaskIdentityList);
					}

					emc.commit();
				}

				ActionResult<Wo> result = new ActionResult<>();
				Wo wo = new Wo();
				wo.setValue(true);
				result.setData(wo);
				return result;
			}
		};

		return ProcessPlatformExecutorFactory.get(job).submit(callable).get();

	}

	private TaskCompleted getTaskCompleted(Business business, String taskCompletedId) throws Exception {
		TaskCompleted taskCompleted = business.entityManagerContainer().find(taskCompletedId, TaskCompleted.class);
		if (null == taskCompleted) {
			throw new ExceptionEntityNotExist(taskCompletedId, TaskCompleted.class);
		}
		return taskCompleted;
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

	private void update(Work work, WorkLog workLog) {
		work.setActivity(workLog.getFromActivity());
		work.setActivityAlias(workLog.getFromActivityAlias());
		work.setActivityName(workLog.getFromActivityName());
		work.setActivityToken(workLog.getFromActivityToken());
		work.setSplitting(workLog.getSplitting());
		work.setSplitToken(workLog.getSplitToken());
		work.setSplitValue(workLog.getSplitValue());
		workLog.setConnected(false);
	}

	private WorkLog getTargetWorkLog(List<WorkLog> list, String id) throws Exception {
		WorkLog workLog = list.stream().filter(o -> StringUtils.equals(o.getId(), id)).findFirst().orElse(null);
		if (null == workLog) {
			throw new ExceptionEntityNotExist(id, WorkLog.class);
		}
		return workLog;
	}

	private List<String> activityTokenOfNodes(Nodes nodes) throws Exception {
		List<String> list = new ArrayList<>();
		for (Node o : nodes) {
			list.add(o.getWorkLog().getFromActivityToken());
		}
		return ListTools.trim(list, true, true);
	}

	private List<String> workOfNodes(Nodes nodes) throws Exception {
		List<String> list = new ArrayList<>();
		for (Node o : nodes) {
			list.add(o.getWorkLog().getWork());
		}
		return ListTools.trim(list, true, true);
	}

	private void deleteTasks(Business business, String job, List<String> activityTokens) throws Exception {
		List<Task> os = business.entityManagerContainer().listEqualAndIn(Task.class, Task.job_FIELDNAME, job,
				Task.activityToken_FIELDNAME, activityTokens);
		for (Task o : os) {
			business.entityManagerContainer().remove(o, CheckRemoveType.all);
		}
	}

	private void deleteTaskCompleteds(Business business, String job, List<String> activityTokens) throws Exception {
		List<TaskCompleted> os = business.entityManagerContainer().listEqualAndIn(TaskCompleted.class,
				TaskCompleted.job_FIELDNAME, job, TaskCompleted.activityToken_FIELDNAME, activityTokens);
		for (TaskCompleted o : os) {
			business.entityManagerContainer().remove(o, CheckRemoveType.all);
		}
	}

	private void deleteReads(Business business, String job, List<String> activityTokens) throws Exception {
		List<Read> os = business.entityManagerContainer().listEqualAndIn(Read.class, Read.job_FIELDNAME, job,
				Read.activityToken_FIELDNAME, activityTokens);
		for (Read o : os) {
			business.entityManagerContainer().remove(o, CheckRemoveType.all);
		}
	}

	private void deleteReadCompleteds(Business business, String job, List<String> activityTokens) throws Exception {
		List<ReadCompleted> os = business.entityManagerContainer().listEqualAndIn(ReadCompleted.class,
				ReadCompleted.job_FIELDNAME, job, ReadCompleted.activityToken_FIELDNAME, activityTokens);
		for (ReadCompleted o : os) {
			business.entityManagerContainer().remove(o, CheckRemoveType.all);
		}
	}

	private void deleteRecords(Business business, String job, List<String> activityTokens) throws Exception {
		List<Record> os = business.entityManagerContainer().listEqualAndIn(Record.class, Record.job_FIELDNAME, job,
				Record.fromActivityToken_FIELDNAME, activityTokens);
		for (Record o : os) {
			business.entityManagerContainer().remove(o, CheckRemoveType.all);
		}
	}

	private void deleteWorkLogs(Business business, String job, List<String> activityTokens) throws Exception {
		List<WorkLog> os = business.entityManagerContainer().listEqualAndIn(WorkLog.class, WorkLog.job_FIELDNAME, job,
				WorkLog.fromActivityToken_FIELDNAME, activityTokens);
		for (WorkLog o : os) {
			business.entityManagerContainer().remove(o, CheckRemoveType.all);
		}
	}

	private void deleteWorks(Business business, String job, List<String> workIds) throws Exception {
		List<Work> os = business.entityManagerContainer().listEqualAndIn(Work.class, Work.job_FIELDNAME, job,
				Work.id_FIELDNAME, workIds);
		for (Work o : os) {
			business.entityManagerContainer().remove(o, CheckRemoveType.all);
		}
	}

	public static class Wi extends V2RetractWi {

	}

	public static class Wo extends WrapBoolean {
	}
}