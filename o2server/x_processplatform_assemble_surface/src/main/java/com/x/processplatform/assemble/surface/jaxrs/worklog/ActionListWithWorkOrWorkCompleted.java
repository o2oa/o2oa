package com.x.processplatform.assemble.surface.jaxrs.worklog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListWithWorkOrWorkCompleted extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithWorkOrWorkCompleted.class);

	private static final String TASKLIST_FIELDNAME = "taskList";
	private static final String TASKCOMPLETEDLIST_FIELDNAME = "taskCompletedList";
	private static final String READLIST_FIELDNAME = "readList";
	private static final String READCOMPLETEDLIST_FIELDNAME = "readCompletedList";

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String workOrWorkCompleted) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		String job = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			job = business.job().findWithWorkOrWorkCompleted(workOrWorkCompleted);
		}

		final String workLogJob = job;

		CompletableFuture<List<WoTask>> tasksFuture = CompletableFuture.supplyAsync(() -> this.tasks(workLogJob),
				ThisApplication.forkJoinPool());
		CompletableFuture<List<WoTaskCompleted>> taskCompletedsFuture = CompletableFuture
				.supplyAsync(() -> this.taskCompleteds(workLogJob), ThisApplication.forkJoinPool());
		CompletableFuture<List<WoRead>> readsFuture = CompletableFuture.supplyAsync(() -> this.reads(workLogJob),
				ThisApplication.forkJoinPool());
		CompletableFuture<List<WoReadCompleted>> readCompletedsFuture = CompletableFuture
				.supplyAsync(() -> this.readCompleteds(workLogJob), ThisApplication.forkJoinPool());
		CompletableFuture<List<WorkLog>> workLogsFuture = CompletableFuture.supplyAsync(() -> this.workLogs(workLogJob),
				ThisApplication.forkJoinPool());
		CompletableFuture<Boolean> controlFuture = CompletableFuture.supplyAsync(() -> {
			Boolean value = false;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Control control = new JobControlBuilder(effectivePerson, business, workLogJob).enableAllowVisit()
						.build();
				value = control.getAllowVisit();
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return value;
		}, ThisApplication.forkJoinPool());

		if (BooleanUtils.isFalse(controlFuture.get())) {
			throw new ExceptionAccessDenied(effectivePerson, workOrWorkCompleted);
		}
		List<WoTask> tasks = tasksFuture.get();
		List<WoTaskCompleted> taskCompleteds = taskCompletedsFuture.get();
		List<WoRead> reads = readsFuture.get();
		List<WoReadCompleted> readCompleteds = readCompletedsFuture.get();
		List<WorkLog> workLogs = workLogsFuture.get();

		if (!workLogs.isEmpty()) {
			WorkLogTree tree = new WorkLogTree(workLogs);
			List<Wo> wos = new ArrayList<>();
			for (WorkLog o : workLogs) {
				Wo wo = Wo.copier.copy(o);
				Node node = tree.find(o);
				if (null != node) {
					Nodes nodes = node.downNextManual();
					if (nodes.isEmpty()) {
						// 如果没有找到后面的人工节点,那么有多种可能,有一种是已经删除,工作合并到其他分支了,那么找其他分支的下一步
						WorkLog otherWorkLog = workLogs.stream()
								.filter(g -> (g != o)
										&& StringUtils.equals(g.getArrivedActivity(), o.getArrivedActivity())
										&& StringUtils.equals(g.getSplitToken(), o.getSplitToken()))
								.findFirst().orElse(null);
						if (null != otherWorkLog) {
							node = tree.find(otherWorkLog);
							if (null != node) {
								nodes = node.downNextManual();
							}
						}
					}
					if (!nodes.isEmpty()) {
						for (Node n : nodes) {
							tasks.stream().filter(t -> StringUtils.equals(t.getActivityToken(),
									n.getWorkLog().getFromActivityToken())).forEach(t -> {
										wo.getNextTaskIdentityList().add(t.getIdentity());
									});
							taskCompleteds.stream()
									.filter(t -> BooleanUtils.isTrue(t.getJoinInquire()) && StringUtils
											.equals(t.getActivityToken(), n.getWorkLog().getFromActivityToken()))
									.forEach(t -> {
										wo.getNextTaskCompletedIdentityList().add(t.getIdentity());
									});
						}
					}
				}
				// 下一环节处理人可能是重复处理导致重复的,去重
				wo.setNextTaskIdentityList(ListTools.trim(wo.getNextTaskIdentityList(), true, true));
				wo.setNextTaskCompletedIdentityList(ListTools.trim(wo.getNextTaskCompletedIdentityList(), true, true));
				wos.add(wo);
			}
			ListTools.groupStick(wos, tasks, WorkLog.FROMACTIVITYTOKEN_FIELDNAME, Task.activityToken_FIELDNAME,
					TASKLIST_FIELDNAME);
			ListTools.groupStick(wos, taskCompleteds, WorkLog.FROMACTIVITYTOKEN_FIELDNAME,
					TaskCompleted.activityToken_FIELDNAME, TASKCOMPLETEDLIST_FIELDNAME);
			ListTools.groupStick(wos, reads, WorkLog.FROMACTIVITYTOKEN_FIELDNAME, Read.activityToken_FIELDNAME,
					READLIST_FIELDNAME);
			ListTools.groupStick(wos, readCompleteds, WorkLog.FROMACTIVITYTOKEN_FIELDNAME,
					ReadCompleted.activityToken_FIELDNAME, READCOMPLETEDLIST_FIELDNAME);
			result.setData(wos);
		}
		return result;
	}

	private List<WoTask> tasks(String job) {
		List<WoTask> os = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			os = WoTask.copier.copy(emc.listEqual(Task.class, Task.job_FIELDNAME, job).stream()
					.sorted(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Date::compareTo)))
					.collect(Collectors.toList()));
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return os;
	}

	private List<WoTaskCompleted> taskCompleteds(String job) {
		List<WoTaskCompleted> os = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			os = emc.fetchEqual(TaskCompleted.class, WoTaskCompleted.copier, TaskCompleted.job_FIELDNAME, job).stream()
					.sorted(Comparator.comparing(TaskCompleted::getStartTime, Comparator.nullsLast(Date::compareTo)))
					.collect(Collectors.toList());
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return os;
	}

	private List<WoRead> reads(String job) {
		List<WoRead> os = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			os = emc.fetchEqual(Read.class, WoRead.copier, Read.job_FIELDNAME, job).stream()
					.sorted(Comparator.comparing(Read::getStartTime, Comparator.nullsLast(Date::compareTo)))
					.collect(Collectors.toList());
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return os;
	}

	private List<WoReadCompleted> readCompleteds(String job) {
		List<WoReadCompleted> os = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			os = emc.fetchEqual(ReadCompleted.class, WoReadCompleted.copier, ReadCompleted.job_FIELDNAME, job).stream()
					.sorted(Comparator.comparing(ReadCompleted::getStartTime, Comparator.nullsLast(Date::compareTo)))
					.collect(Collectors.toList());
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return os;
	}

	private List<WorkLog> workLogs(String job) {
		List<WorkLog> os = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			os = emc.listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, job);
			return os.stream()
					.sorted(Comparator.comparing(WorkLog::getFromTime, Comparator.nullsLast(Date::compareTo))
							.thenComparing(WorkLog::getArrivedTime, Comparator.nullsLast(Date::compareTo)))
					.collect(Collectors.toList());
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return os;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.worklog.ActionListWithWorkOrWorkCompleted$Wo")
	public static class Wo extends WorkLog {

		private static final long serialVersionUID = -7666329770246726197L;

		static WrapCopier<WorkLog, Wo> copier = WrapCopierFactory.wo(WorkLog.class, Wo.class,
				ListTools.toList(JpaObject.id_FIELDNAME, WorkLog.FROMACTIVITY_FIELDNAME,
						WorkLog.FROMACTIVITYTYPE_FIELDNAME, WorkLog.FROMACTIVITYNAME_FIELDNAME,
						WorkLog.FROMACTIVITYALIAS_FIELDNAME, WorkLog.FROMACTIVITYTOKEN_FIELDNAME,
						WorkLog.FROMTIME_FIELDNAME, WorkLog.ARRIVEDACTIVITY_FIELDNAME,
						WorkLog.ARRIVEDACTIVITYTYPE_FIELDNAME, WorkLog.ARRIVEDACTIVITYNAME_FIELDNAME,
						WorkLog.ARRIVEDACTIVITYALIAS_FIELDNAME, WorkLog.ARRIVEDACTIVITYTOKEN_FIELDNAME,
						WorkLog.ARRIVEDTIME_FIELDNAME, WorkLog.ROUTENAME_FIELDNAME, WorkLog.ROUTE_FIELDNAME,
						WorkLog.CONNECTED_FIELDNAME, WorkLog.SPLITTING_FIELDNAME, WorkLog.FROMGROUP_FIELDNAME,
						WorkLog.ARRIVEDGROUP_FIELDNAME, WorkLog.FROMOPINIONGROUP_FIELDNAME,
						WorkLog.ARRIVEDOPINIONGROUP_FIELDNAME),
				JpaObject.FieldsInvisible);

		private List<WoTask> taskList = new ArrayList<>();

		private List<WoTaskCompleted> taskCompletedList = new ArrayList<>();

		private List<WoRead> readList = new ArrayList<>();

		private List<WoReadCompleted> readCompletedList = new ArrayList<>();

		private List<String> nextTaskIdentityList = new ArrayList<>();

		private List<String> nextTaskCompletedIdentityList = new ArrayList<>();

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

		public List<String> getNextTaskIdentityList() {
			return nextTaskIdentityList;
		}

		public void setNextTaskIdentityList(List<String> nextTaskIdentityList) {
			this.nextTaskIdentityList = nextTaskIdentityList;
		}

		public List<String> getNextTaskCompletedIdentityList() {
			return nextTaskCompletedIdentityList;
		}

		public void setNextTaskCompletedIdentityList(List<String> nextTaskCompletedIdentityList) {
			this.nextTaskCompletedIdentityList = nextTaskCompletedIdentityList;
		}

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.worklog.ActionListWithWorkOrWorkCompleted$WoTask")
	public static class WoTask extends Task {

		private static final long serialVersionUID = 293599148568443301L;

		static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class,
				ListTools.toList(JpaObject.id_FIELDNAME, Task.person_FIELDNAME, Task.identity_FIELDNAME,
						Task.unit_FIELDNAME, Task.ROUTENAME_FIELDNAME, Task.opinion_FIELDNAME,
						Task.opinionLob_FIELDNAME, Task.startTime_FIELDNAME, Task.activityName_FIELDNAME,
						Task.activityToken_FIELDNAME, Task.empowerFromIdentity_FIELDNAME, Task.properties_FIELDNAME),
				null);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.worklog.ActionListWithWorkOrWorkCompleted$WoTaskCompleted")
	public static class WoTaskCompleted extends TaskCompleted {

		private static final long serialVersionUID = -4432508672641778924L;

		static WrapCopier<TaskCompleted, WoTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
				WoTaskCompleted.class,
				ListTools.toList(JpaObject.id_FIELDNAME, TaskCompleted.person_FIELDNAME,
						TaskCompleted.identity_FIELDNAME, TaskCompleted.unit_FIELDNAME,
						TaskCompleted.ROUTENAME_FIELDNAME, TaskCompleted.opinion_FIELDNAME,
						TaskCompleted.opinionLob_FIELDNAME, TaskCompleted.startTime_FIELDNAME,
						TaskCompleted.activityName_FIELDNAME, TaskCompleted.completedTime_FIELDNAME,
						TaskCompleted.activityToken_FIELDNAME, TaskCompleted.mediaOpinion_FIELDNAME,
						TaskCompleted.processingType_FIELDNAME, TaskCompleted.empowerToIdentity_FIELDNAME,
						TaskCompleted.empowerFromIdentity_FIELDNAME, TaskCompleted.joinInquire_FIELDNAME,
						TaskCompleted.properties_FIELDNAME, TaskCompleted.properties_FIELDNAME),
				null);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.worklog.ActionListWithWorkOrWorkCompleted$WoRead")
	public static class WoRead extends Read {

		private static final long serialVersionUID = -7243683008987722267L;

		static WrapCopier<Read, WoRead> copier = WrapCopierFactory.wo(Read.class, WoRead.class,
				ListTools.toList(JpaObject.id_FIELDNAME, Read.person_FIELDNAME, Read.identity_FIELDNAME,
						Read.unit_FIELDNAME, Read.opinion_FIELDNAME, Read.opinionLob_FIELDNAME,
						Read.startTime_FIELDNAME, Read.activityName_FIELDNAME, Read.activityToken_FIELDNAME,
						Read.properties_FIELDNAME),
				null);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.worklog.ActionListWithWorkOrWorkCompleted$WoReadCompleted")
	public static class WoReadCompleted extends ReadCompleted {

		private static final long serialVersionUID = -7086077858353505033L;

		static WrapCopier<ReadCompleted, WoReadCompleted> copier = WrapCopierFactory.wo(ReadCompleted.class,
				WoReadCompleted.class,
				ListTools.toList(JpaObject.id_FIELDNAME, ReadCompleted.person_FIELDNAME,
						ReadCompleted.identity_FIELDNAME, ReadCompleted.unit_FIELDNAME, ReadCompleted.opinion_FIELDNAME,
						ReadCompleted.opinionLob_FIELDNAME, ReadCompleted.startTime_FIELDNAME,
						ReadCompleted.activityName_FIELDNAME, ReadCompleted.completedTime_FIELDNAME,
						ReadCompleted.activityToken_FIELDNAME, ReadCompleted.properties_FIELDNAME),
				null);
	}

}