package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.core.entity.content.WorkLog;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionReference extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			TaskCompleted taskCompleted = emc.find(id, TaskCompleted.class);
			if (null == taskCompleted) {
				throw new ExceptionEntityNotExist(id, TaskCompleted.class);
			}
			Wo wo = new Wo();
			wo.setTaskCompleted(WoTaskCompleted.copier.copy(taskCompleted));
			wo.setWorkCompletedList(this.listWorkCompleted(business, taskCompleted));
			wo.setWorkLogList(this.listWorkLog(business, taskCompleted));
			wo.setWorkList(this.listWork(business, taskCompleted));
			result.setData(wo);
			return result;
		}
	}

	private List<WoWorkLog> listWorkLog(Business business, TaskCompleted taskCompleted) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.workLog().listWithJob(taskCompleted.getJob());
		List<WorkLog> os = emc.list(WorkLog.class, ids);
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
		if (BooleanUtils.isNotTrue(workLog.getConnected())) {
			referenceWorkLogTask(business, wo);
		} else {
			/* 已经完成的不会有待办，返回一个空数组 */
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
//			if (o.getProcessingType().equals(ProcessingType.retract)) {
//				WoTaskCompleted retract = new WoTaskCompleted();
//				o.copyTo(retract);
//				retract.setRouteName("撤回");
//				retract.setOpinion("撤回");
//				retract.setStartTime(retract.getRetractTime());
//				retract.setCompletedTime(retract.getRetractTime());
//				list.add(retract);
//			}
//		}
//		wo.setTaskCompletedList(list);
		wo.setTaskCompletedList(WoTaskCompleted.copier.copy(os));
	}

	// @TODO
	private List<WoWork> listWork(Business business, TaskCompleted taskCompleted) throws Exception {
		List<String> workIds = business.work().listWithJob(taskCompleted.getJob());
//		List<String> ids = business.workLog().listWithFromActivityTokenForward(taskCompleted.getActivityToken());
//		List<String> workIds = SetUniqueList.setUniqueList(new ArrayList<String>());
//		for (WorkLog o : business.entityManagerContainer().list(WorkLog.class, ids)) {
//			workIds.add(o.getWork());
//		}
		return WoWork.copier.copy(business.entityManagerContainer().list(Work.class, workIds));
	}

	private List<WoWorkCompleted> listWorkCompleted(Business business, TaskCompleted taskCompleted) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WorkCompleted> cq = cb.createQuery(WorkCompleted.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.job), taskCompleted.getJob());
		cq.select(root).where(p);
		List<WorkCompleted> list = em.createQuery(cq).getResultList();
		List<WoWorkCompleted> wos = WoWorkCompleted.copier.copy(list);
		wos = wos.stream()
				.sorted(Comparator.comparing(WoWorkCompleted::getCreateTime, Comparator.nullsLast(Date::compareTo)))
				.collect(Collectors.toList());
		return wos;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.taskcompleted.ActionReference$Wo")
	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 6314327570968988398L;

		@FieldDescribe("已办对象")
		private WoTaskCompleted taskCompleted;

		@FieldDescribe("工作对象")
		private List<WoWork> workList = new ArrayList<>();

		@FieldDescribe("已完成工作对象")
		private List<WoWorkCompleted> workCompletedList = new ArrayList<>();

		@FieldDescribe("工作记录对象")
		private List<WoWorkLog> workLogList = new ArrayList<>();

		public WoTaskCompleted getTaskCompleted() {
			return taskCompleted;
		}

		public void setTaskCompleted(WoTaskCompleted taskCompleted) {
			this.taskCompleted = taskCompleted;
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

		public List<WoWorkLog> getWorkLogList() {
			return workLogList;
		}

		public void setWorkLogList(List<WoWorkLog> workLogList) {
			this.workLogList = workLogList;
		}
	}

	public static class WoTaskCompleted extends TaskCompleted {

		private static final long serialVersionUID = -7253999118308715077L;

		static WrapCopier<TaskCompleted, WoTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
				WoTaskCompleted.class, null, JpaObject.FieldsInvisible);
	}

	public static class WoWork extends Work {

		private static final long serialVersionUID = -5668264661685818057L;

		static WrapCopier<Work, WoWork> copier = WrapCopierFactory.wo(Work.class, WoWork.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class WoWorkCompleted extends WorkCompleted {

		private static final long serialVersionUID = 2395048971976018595L;

		static WrapCopier<WorkCompleted, WoWorkCompleted> copier = WrapCopierFactory.wo(WorkCompleted.class,
				WoWorkCompleted.class, null, ListTools.toList(JpaObject.FieldsInvisible, "formData", "formMobileData"));

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

}