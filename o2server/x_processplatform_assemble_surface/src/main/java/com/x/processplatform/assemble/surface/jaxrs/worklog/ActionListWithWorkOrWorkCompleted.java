package com.x.processplatform.assemble.surface.jaxrs.worklog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkLog;

class ActionListWithWorkOrWorkCompleted extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListWithWorkOrWorkCompleted.class);

	private final static String taskList_FIELDNAME = "taskList";
	private final static String taskCompletedList_FIELDNAME = "taskCompletedList";
	private final static String readList_FIELDNAME = "readList";
	private final static String readCompletedList_FIELDNAME = "readCompletedList";

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String workOrWorkCompleted) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();

			Business business = new Business(emc);

			if (!business.readableWithWorkOrWorkCompleted(effectivePerson, workOrWorkCompleted,
					new ExceptionEntityNotExist(workOrWorkCompleted))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			final String job = business.job().findWithWorkOrWorkCompleted(workOrWorkCompleted);

			CompletableFuture<List<WoTask>> future_tasks = CompletableFuture.supplyAsync(() -> {
				return this.tasks(business, job);
			});
			CompletableFuture<List<WoTaskCompleted>> future_taskCompleteds = CompletableFuture.supplyAsync(() -> {
				return this.taskCompleteds(business, job);
			});
			CompletableFuture<List<WoRead>> future_reads = CompletableFuture.supplyAsync(() -> {
				return this.reads(business, job);
			});
			CompletableFuture<List<WoReadCompleted>> future_readCompleteds = CompletableFuture.supplyAsync(() -> {
				return this.readCompleteds(business, job);
			});
			CompletableFuture<List<Wo>> future_workLogs = CompletableFuture.supplyAsync(() -> {
				return this.workLogs(business, job);
			});
			List<WoTask> tasks = future_tasks.get();
			List<WoTaskCompleted> taskCompleteds = future_taskCompleteds.get();
			List<WoRead> reads = future_reads.get();
			List<WoReadCompleted> readCompleteds = future_readCompleteds.get();
			List<Wo> wos = future_workLogs.get();
			ListTools.groupStick(wos, tasks, WorkLog.fromActivityToken_FIELDNAME, Task.activityToken_FIELDNAME,
					taskList_FIELDNAME);
			ListTools.groupStick(wos, taskCompleteds, WorkLog.fromActivityToken_FIELDNAME,
					TaskCompleted.activityToken_FIELDNAME, taskCompletedList_FIELDNAME);
			ListTools.groupStick(wos, reads, WorkLog.fromActivityToken_FIELDNAME, Read.activityToken_FIELDNAME,
					readList_FIELDNAME);
			ListTools.groupStick(wos, readCompleteds, WorkLog.fromActivityToken_FIELDNAME,
					ReadCompleted.activityToken_FIELDNAME, readCompletedList_FIELDNAME);
			result.setData(wos);
			return result;
		}
	}

	private List<WoTask> tasks(Business business, String job) {
		List<WoTask> os = new ArrayList<>();
		try {
			os = business.entityManagerContainer().fetchEqual(Task.class, WoTask.copier, WoTask.job_FIELDNAME, job)
					.stream().sorted(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Date::compareTo)))
					.collect(Collectors.toList());
		} catch (Exception e) {
			logger.error(e);
		}
		return os;
	}

	private List<WoTaskCompleted> taskCompleteds(Business business, String job) {
		List<WoTaskCompleted> os = new ArrayList<>();
		try {
			os = business.entityManagerContainer()
					.fetchEqual(TaskCompleted.class, WoTaskCompleted.copier, TaskCompleted.job_FIELDNAME, job).stream()
					.sorted(Comparator.comparing(TaskCompleted::getStartTime, Comparator.nullsLast(Date::compareTo)))
					.collect(Collectors.toList());
		} catch (Exception e) {
			logger.error(e);
		}
		return os;
	}

	private List<WoRead> reads(Business business, String job) {
		List<WoRead> os = new ArrayList<>();
		try {
			os = business.entityManagerContainer().fetchEqual(Read.class, WoRead.copier, Read.job_FIELDNAME, job)
					.stream().sorted(Comparator.comparing(Read::getStartTime, Comparator.nullsLast(Date::compareTo)))
					.collect(Collectors.toList());
		} catch (Exception e) {
			logger.error(e);
		}
		return os;
	}

	private List<WoReadCompleted> readCompleteds(Business business, String job) {
		List<WoReadCompleted> os = new ArrayList<>();
		try {
			os = business.entityManagerContainer()
					.fetchEqual(ReadCompleted.class, WoReadCompleted.copier, ReadCompleted.job_FIELDNAME, job).stream()
					.sorted(Comparator.comparing(ReadCompleted::getStartTime, Comparator.nullsLast(Date::compareTo)))
					.collect(Collectors.toList());
		} catch (Exception e) {
			logger.error(e);
		}
		return os;
	}

	private List<Wo> workLogs(Business business, String job) {
		List<Wo> os = new ArrayList<>();
		try {
			os = business.entityManagerContainer().fetchEqual(WorkLog.class, Wo.copier, WorkLog.job_FIELDNAME, job);
			return os.stream()
					.sorted(Comparator.comparing(Wo::getFromTime, Comparator.nullsLast(Date::compareTo))
							.thenComparing(Wo::getArrivedTime, Comparator.nullsLast(Date::compareTo)))
					.collect(Collectors.toList());
		} catch (Exception e) {
			logger.error(e);
		}
		return os;
	}

	public static class Wo extends WorkLog {

		private static final long serialVersionUID = -7666329770246726197L;

		static WrapCopier<WorkLog, Wo> copier = WrapCopierFactory.wo(WorkLog.class, Wo.class,
				ListTools.toList(WorkLog.id_FIELDNAME, WorkLog.fromActivity_FIELDNAME,
						WorkLog.fromActivityType_FIELDNAME, WorkLog.fromActivityName_FIELDNAME,
						WorkLog.fromActivityAlias_FIELDNAME, WorkLog.fromActivityToken_FIELDNAME,
						WorkLog.fromTime_FIELDNAME, WorkLog.arrivedActivity_FIELDNAME,
						WorkLog.arrivedActivityType_FIELDNAME, WorkLog.arrivedActivityName_FIELDNAME,
						WorkLog.arrivedActivityAlias_FIELDNAME, WorkLog.arrivedActivityToken_FIELDNAME,
						WorkLog.arrivedTime_FIELDNAME, WorkLog.routeName_FIELDNAME, WorkLog.connected_FIELDNAME,
						WorkLog.splitting_FIELDNAME, WorkLog.fromGroup_FIELDNAME, WorkLog.arrivedGroup_FIELDNAME,
						WorkLog.fromOpinionGroup_FIELDNAME, WorkLog.arrivedOpinionGroup_FIELDNAME),
				JpaObject.FieldsInvisible);

		private List<WoTask> taskList = new ArrayList<>();

		private List<WoTaskCompleted> taskCompletedList = new ArrayList<>();

		private List<WoRead> readList = new ArrayList<>();

		private List<WoReadCompleted> readCompletedList = new ArrayList<>();

		public List<WoTask> getTaskList() {
			return taskList;
		}

		public void setTaskList(List<WoTask> taskList) {
			this.taskList = taskList;
		}

		public List<WoTaskCompleted> getTaskCompletedList() {
			return taskCompletedList;
		}

		public void setTaskCompletedList(List<WoTaskCompleted> taskCompletedList) {
			this.taskCompletedList = taskCompletedList;
		}

		public List<WoRead> getReadList() {
			return readList;
		}

		public void setReadList(List<WoRead> readList) {
			this.readList = readList;
		}

		public List<WoReadCompleted> getReadCompletedList() {
			return readCompletedList;
		}

		public void setReadCompletedList(List<WoReadCompleted> readCompletedList) {
			this.readCompletedList = readCompletedList;
		}

	}

	public static class WoTask extends Task {

		private static final long serialVersionUID = 293599148568443301L;

		static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class,
				ListTools.toList(Task.id_FIELDNAME, Task.person_FIELDNAME, Task.identity_FIELDNAME, Task.unit_FIELDNAME,
						Task.routeName_FIELDNAME, Task.opinion_FIELDNAME, Task.opinionLob_FIELDNAME,
						Task.startTime_FIELDNAME, Task.activityName_FIELDNAME, Task.activityToken_FIELDNAME),
				null);
	}

	public static class WoTaskCompleted extends TaskCompleted {

		private static final long serialVersionUID = -4432508672641778924L;

		static WrapCopier<TaskCompleted, WoTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
				WoTaskCompleted.class,
				ListTools.toList(TaskCompleted.id_FIELDNAME, TaskCompleted.person_FIELDNAME,
						TaskCompleted.identity_FIELDNAME, TaskCompleted.unit_FIELDNAME,
						TaskCompleted.routeName_FIELDNAME, TaskCompleted.opinion_FIELDNAME,
						TaskCompleted.opinionLob_FIELDNAME, TaskCompleted.startTime_FIELDNAME,
						TaskCompleted.activityName_FIELDNAME, TaskCompleted.completedTime_FIELDNAME,
						TaskCompleted.activityToken_FIELDNAME, TaskCompleted.mediaOpinion_FIELDNAME,
						TaskCompleted.processingType_FIELDNAME),
				null);
	}

	public static class WoRead extends Read {

		private static final long serialVersionUID = -7243683008987722267L;

		static WrapCopier<Read, WoRead> copier = WrapCopierFactory.wo(Read.class, WoRead.class,
				ListTools.toList(Read.id_FIELDNAME, Read.person_FIELDNAME, Read.identity_FIELDNAME, Read.unit_FIELDNAME,
						Read.opinion_FIELDNAME, Read.opinionLob_FIELDNAME, Read.startTime_FIELDNAME,
						Read.activityName_FIELDNAME, Read.activityToken_FIELDNAME),
				null);
	}

	public static class WoReadCompleted extends ReadCompleted {

		private static final long serialVersionUID = -7086077858353505033L;

		static WrapCopier<ReadCompleted, WoReadCompleted> copier = WrapCopierFactory.wo(ReadCompleted.class,
				WoReadCompleted.class,
				ListTools.toList(ReadCompleted.id_FIELDNAME, ReadCompleted.person_FIELDNAME,
						ReadCompleted.identity_FIELDNAME, ReadCompleted.unit_FIELDNAME, ReadCompleted.opinion_FIELDNAME,
						ReadCompleted.opinionLob_FIELDNAME, ReadCompleted.startTime_FIELDNAME,
						ReadCompleted.activityName_FIELDNAME, ReadCompleted.completedTime_FIELDNAME,
						ReadCompleted.activityToken_FIELDNAME),
				null);
	}

}