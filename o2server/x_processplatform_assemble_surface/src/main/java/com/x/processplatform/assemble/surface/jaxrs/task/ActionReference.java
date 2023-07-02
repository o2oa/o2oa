package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Attachment;
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
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Task task = emc.find(id, Task.class);
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			if (effectivePerson.isNotPerson(effectivePerson.getDistinguishedName()) && effectivePerson.isNotManager()) {
				throw new ExceptionAccessDenied(effectivePerson, task);
			}
			Wo wo = new Wo();
			// 组装 Task 信息
			wo.setTask(WoTask.copier.copy(task));
			Work work = emc.find(task.getWork(), Work.class);
			// 组装 Work
			if (null != work) {
				wo.setWork(WoWork.copier.copy(work));
				// 组装 Attachment
				wo.setAttachmentList(this.listAttachment(business, work));
			}
			wo.setWorkCompletedList(this.listWorkCompleted(business, task));
			// 装载WorkLog 信息
			wo.setWorkLogList(this.listWorkLog(business, task));
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionReference.Wo")
	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("待办对象.")
		@Schema(description = "待办对象.")
		private WoTask task;
		@FieldDescribe("工作对象.")
		@Schema(description = "工作对象.")
		private WoWork work;
		@FieldDescribe("附件对象")
		@Schema(description = "工作对象.")
		private List<WoAttachment> attachmentList = new ArrayList<>();
		@FieldDescribe("已完成工作对象")
		private List<WoWorkCompleted> workCompletedList = new ArrayList<>();
		@FieldDescribe("工作日志对象")
		private List<WoWorkLog> workLogList = new ArrayList<>();

		public WoTask getTask() {
			return task;
		}

		public void setTask(WoTask task) {
			this.task = task;
		}

		public WoWork getWork() {
			return work;
		}

		public void setWork(WoWork work) {
			this.work = work;
		}

		public List<WoAttachment> getAttachmentList() {
			return attachmentList;
		}

		public void setAttachmentList(List<WoAttachment> attachmentList) {
			this.attachmentList = attachmentList;
		}

		public List<WoWorkCompleted> getWorkCompletedList() {
			return workCompletedList;
		}

		public void setWorkCompletedList(List<WoWorkCompleted> workCompletedList) {
			this.workCompletedList = workCompletedList;
		}

		public List<WoWorkLog> getWorkLogList() {
			return workLogList;
		}

		public void setWorkLogList(List<WoWorkLog> workLogList) {
			this.workLogList = workLogList;
		}

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionReference.WoWorkCompleted")
	public static class WoWorkCompleted extends WorkCompleted {

		private static final long serialVersionUID = 2395048971976018595L;

		static WrapCopier<WorkCompleted, WoWorkCompleted> copier = WrapCopierFactory.wo(WorkCompleted.class,
				WoWorkCompleted.class, null, ListTools.toList(JpaObject.FieldsInvisible, "formData", "formMobileData"));

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionReference.WoWork")
	public static class WoWork extends Work {

		private static final long serialVersionUID = -5668264661685818057L;

		static WrapCopier<Work, WoWork> copier = WrapCopierFactory.wo(Work.class, WoWork.class, null,
				JpaObject.FieldsInvisible);

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionReference.WoTask")
	public static class WoTask extends Task {

		private static final long serialVersionUID = 2702712453822143654L;

		static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class, null,
				JpaObject.FieldsInvisible);

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionReference.WoAttachment")
	public static class WoAttachment extends Attachment {

		private static final long serialVersionUID = -116515826662248117L;

		static WrapCopier<Attachment, WoAttachment> copier = WrapCopierFactory.wo(Attachment.class, WoAttachment.class,
				null, JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionReference.WoTaskCompleted")
	public static class WoTaskCompleted extends TaskCompleted {

		private static final long serialVersionUID = -7253999118308715077L;

		static WrapCopier<TaskCompleted, WoTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
				WoTaskCompleted.class, null, JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionReference.WoWorkLog")
	public static class WoWorkLog extends WorkLog {

		private static final long serialVersionUID = 1307569946729101786L;

		static WrapCopier<WorkLog, WoWorkLog> copier = WrapCopierFactory.wo(WorkLog.class, WoWorkLog.class, null,
				JpaObject.FieldsInvisible);

		private Long rank;

		private List<WoTaskCompleted> taskCompletedList;

		private List<WoTask> taskList;

		private Integer currentTaskIndex;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public List<WoTaskCompleted> getTaskCompletedList() {
			return taskCompletedList;
		}

		public void setTaskCompletedList(List<WoTaskCompleted> taskCompletedList) {
			this.taskCompletedList = taskCompletedList;
		}

		public List<WoTask> getTaskList() {
			return taskList;
		}

		public void setTaskList(List<WoTask> taskList) {
			this.taskList = taskList;
		}

		public Integer getCurrentTaskIndex() {
			return currentTaskIndex;
		}

		public void setCurrentTaskIndex(Integer currentTaskIndex) {
			this.currentTaskIndex = currentTaskIndex;
		}

	}

	private List<WoAttachment> listAttachment(Business business, Work work) throws Exception {
		List<Attachment> list = business.attachment().listWithJobObject(work.getJob());
		List<WoAttachment> wos = WoAttachment.copier.copy(list);
		wos = wos.stream()
				.sorted(Comparator.comparing(Attachment::getCreateTime, Comparator.nullsLast(Date::compareTo)))
				.collect(Collectors.toList());
		return wos;
	}

	private List<WoWorkLog> listWorkLog(Business business, Task task) throws Exception {
		List<String> ids = business.workLog().listWithFromActivityTokenBackward(task.getActivityToken());
		List<WorkLog> os = business.entityManagerContainer().list(WorkLog.class, ids);
		return this.reference(business, os);
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
			// 已经完成的不会有待办，返回一个空数组
			wo.setTaskList(new ArrayList<>());
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

	public static void referenceWorkLogTaskCompleted(Business business, WoWorkLog wo) throws Exception {
		List<String> ids = business.taskCompleted().listWithActivityToken(wo.getFromActivityToken());
		List<TaskCompleted> os = business.entityManagerContainer().list(TaskCompleted.class, ids);
		os = os.stream()
				.sorted(Comparator.comparing(TaskCompleted::getCompletedTime, Comparator.nullsLast(Date::compareTo)))
				.collect(Collectors.toList());
//		/** 补充召回 */
//		List<WoTaskCompleted> wos = WoTaskCompleted.copier.copy(os);
//		List<WoTaskCompleted> list = new ArrayList<>();
//		for (WoTaskCompleted o : wos) {
//			list.add(o);
//			if (Objects.equal(o.getProcessingType(), ProcessingType.retract)) {
//				WoTaskCompleted retract = new WoTaskCompleted();
//				o.copyTo(retract);
//				retract.setRouteName("撤回");
//				retract.setOpinion("撤回");
//				retract.setStartTime(retract.getRetractTime());
//				retract.setCompletedTime(retract.getRetractTime());
//				list.add(retract);
//			}
//		}
		wo.setTaskCompletedList(WoTaskCompleted.copier.copy(os));
	}

	private List<WoWorkCompleted> listWorkCompleted(Business business, Task task) throws Exception {
		List<WoWorkCompleted> wos = WoWorkCompleted.copier
				.copy(business.workCompleted().listWithJobObject(task.getJob()));
		wos = wos.stream()
				.sorted(Comparator.comparing(WorkCompleted::getCreateTime, Comparator.nullsLast(Date::compareTo)))
				.collect(Collectors.toList());
		return wos;
	}
}