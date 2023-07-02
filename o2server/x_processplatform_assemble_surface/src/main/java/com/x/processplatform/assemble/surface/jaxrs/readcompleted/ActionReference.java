package com.x.processplatform.assemble.surface.jaxrs.readcompleted;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.ProcessingType;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionReference extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionReference.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			ReadCompleted readCompleted = emc.find(id, ReadCompleted.class);
			if (null == readCompleted) {
				throw new ExceptionEntityNotExist(id, ReadCompleted.class);
			}
			Wo wo = new Wo();
			wo.setReadCompleted(WoReadCompleted.copier.copy(readCompleted));
			wo.setWorkList(this.listWork(business, readCompleted));
			wo.setWorkCompletedList(this.listWorkCompleted(business, readCompleted));
			wo.setWorkLogList(this.listWorkLog(business, readCompleted));
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.readcompleted.ActionReference$Wo")
	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -345942746779607416L;

		@FieldDescribe("已阅.")
		@Schema(description = "已阅.")
		private WoReadCompleted readCompleted;

		@FieldDescribe("工作日志.")
		@Schema(description = "工作日志.")
		private List<WoWorkLog> workLogList;

		@FieldDescribe("工作.")
		@Schema(description = "工作.")
		private List<WoWork> workList;

		@FieldDescribe("已完成工作.")
		@Schema(description = "已完成工作.")
		private List<WoWorkCompleted> workCompletedList;

		public List<WoWorkLog> getWorkLogList() {
			return workLogList;
		}

		public void setWorkLogList(List<WoWorkLog> workLogList) {
			this.workLogList = workLogList;
		}

		public List<WoWork> getWorkList() {
			return workList;
		}

		public void setWorkList(List<WoWork> workList) {
			this.workList = workList;
		}

		public List<WoWorkCompleted> getWorkCompletedList() {
			return workCompletedList;
		}

		public void setWorkCompletedList(List<WoWorkCompleted> workCompletedList) {
			this.workCompletedList = workCompletedList;
		}

		public WoReadCompleted getReadCompleted() {
			return readCompleted;
		}

		public void setReadCompleted(WoReadCompleted readCompleted) {
			this.readCompleted = readCompleted;
		}

	}

	public static class WoReadCompleted extends Read {

		private static final long serialVersionUID = 2187102986432672650L;

		static WrapCopier<ReadCompleted, WoReadCompleted> copier = WrapCopierFactory.wo(ReadCompleted.class,
				WoReadCompleted.class, null, JpaObject.FieldsInvisible);
	}

	public static class WoWorkCompleted extends WorkCompleted {

		private static final long serialVersionUID = 2187102986432672650L;

		static WrapCopier<WorkCompleted, WoWorkCompleted> copier = WrapCopierFactory.wo(WorkCompleted.class,
				WoWorkCompleted.class, null, JpaObject.FieldsInvisible);
	}

	public static class WoWork extends Work {

		private static final long serialVersionUID = 2187102986432672650L;

		static WrapCopier<Work, WoWork> copier = WrapCopierFactory.wo(Work.class, WoWork.class, null,
				JpaObject.FieldsInvisible);
	}

	public static class WoTaskCompleted extends TaskCompleted {

		private static final long serialVersionUID = -7253999118308715077L;

		static WrapCopier<TaskCompleted, WoTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
				WoTaskCompleted.class, null, JpaObject.FieldsInvisible);
	}

	public static class WoTask extends Task {

		private static final long serialVersionUID = 2702712453822143654L;

		static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class WoWorkLog extends WorkLog {

		private static final long serialVersionUID = 1307569946729101786L;

		static WrapCopier<WorkLog, WoWorkLog> copier = WrapCopierFactory.wo(WorkLog.class, WoWorkLog.class, null,
				JpaObject.FieldsInvisible);

		private Long rank;

		private List<? extends WoTaskCompleted> taskCompletedList;

		private List<? extends WoTask> taskList;

		private Integer currentTaskIndex;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public Integer getCurrentTaskIndex() {
			return currentTaskIndex;
		}

		public void setCurrentTaskIndex(Integer currentTaskIndex) {
			this.currentTaskIndex = currentTaskIndex;
		}

		public List<? extends WoTaskCompleted> getTaskCompletedList() {
			return taskCompletedList;
		}

		public void setTaskCompletedList(List<? extends WoTaskCompleted> taskCompletedList) {
			this.taskCompletedList = taskCompletedList;
		}

		public List<? extends WoTask> getTaskList() {
			return taskList;
		}

		public void setTaskList(List<? extends WoTask> taskList) {
			this.taskList = taskList;
		}

	}

	private List<WoWorkLog> reference(Business business, List<WorkLog> list) throws Exception {
		List<WoWorkLog> wos = new ArrayList<>();
		for (WorkLog o : list) {
			wos.add(this.reference(business, o));
		}
		wos = business.workLog().sort(wos);
		return wos;
	}

	private WoWorkLog reference(Business business, WorkLog workLog) throws Exception {
		WoWorkLog wo = WoWorkLog.copier.copy(workLog);
		if (!workLog.getConnected()) {
			referenceWorkLogTask(business, wo);
		} else {
			/* 已经完成的不会有待办，返回一个空数组 */
			wo.setTaskList(new ArrayList<WoTask>());
		}
		referenceWorkLogTaskCompleted(business, wo);
		return wo;
	}

	private void referenceWorkLogTask(Business business, WoWorkLog wo) throws Exception {
		List<String> ids = business.task().listWithActivityToken(wo.getFromActivityToken());
		List<WoTask> wos = WoTask.copier.copy(business.entityManagerContainer().list(Task.class, ids));
		wos = wos.stream().sorted(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Date::compareTo)))
				.collect(Collectors.toList());
		wo.setTaskList(wos);
	}

	private void referenceWorkLogTaskCompleted(Business business, WoWorkLog wo) throws Exception {
		List<String> ids = business.taskCompleted().listWithActivityToken(wo.getFromActivityToken());
		List<TaskCompleted> os = business.entityManagerContainer().list(TaskCompleted.class, ids);
		os = os.stream()
				.sorted(Comparator.comparing(TaskCompleted::getCompletedTime, Comparator.nullsLast(Date::compareTo)))
				.collect(Collectors.toList());
		/** 补充召回 */
		List<WoTaskCompleted> wos = WoTaskCompleted.copier.copy(os);
		List<WoTaskCompleted> list = new ArrayList<>();
		for (WoTaskCompleted o : wos) {
			list.add(o);
			if (o.getProcessingType().equals(ProcessingType.retract)) {
				WoTaskCompleted retract = new WoTaskCompleted();
				o.copyTo(retract);
				retract.setRouteName("撤回");
				retract.setOpinion("撤回");
				retract.setStartTime(retract.getRetractTime());
				retract.setCompletedTime(retract.getRetractTime());
				list.add(retract);
			}
		}
		wo.setTaskCompletedList(list);
	}

	private List<WoWorkLog> listWorkLog(Business business, ReadCompleted readCompleted) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.workLog().listWithJob(readCompleted.getJob());
		List<WorkLog> os = emc.list(WorkLog.class, ids);
		return this.reference(business, os);
	}

	private List<WoWork> listWork(Business business, ReadCompleted readCompleted) throws Exception {
		List<String> ids = business.workLog().listWithFromActivityTokenForward(readCompleted.getActivityToken());
		List<String> workIds = SetUniqueList.setUniqueList(new ArrayList<String>());
		for (WorkLog o : business.entityManagerContainer().list(WorkLog.class, ids)) {
			workIds.add(o.getWork());
		}
		List<WoWork> wos = WoWork.copier.copy(business.entityManagerContainer().list(Work.class, workIds));
		wos = wos.stream().sorted(Comparator.comparing(Work::getCreateTime, Comparator.nullsLast(Date::compareTo)))
				.collect(Collectors.toList());
		return wos;
	}

	private List<WoWorkCompleted> listWorkCompleted(Business business, ReadCompleted readCompleted) throws Exception {
		List<WoWorkCompleted> wos = new ArrayList<>();
		if (BooleanUtils.isTrue(readCompleted.getCompleted())) {
			WorkCompleted o = business.entityManagerContainer().find(readCompleted.getWorkCompleted(),
					WorkCompleted.class);
			if (null != o) {
				wos.add(WoWorkCompleted.copier.copy(o));
			}
		}
		wos = wos.stream()
				.sorted(Comparator.comparing(WorkCompleted::getCreateTime, Comparator.nullsLast(Date::compareTo)))
				.collect(Collectors.toList());
		return wos;
	}

}