package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;

class V3RetractStage extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V3RetractStage.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job) throws Exception {

		LOGGER.debug("execute:{}, job:{}.", effectivePerson::getDistinguishedName, () -> job);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		result.setData(wo);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<TaskCompleted> taskCompleteds = emc.listEqualAndEqualAndEqual(TaskCompleted.class,
					TaskCompleted.person_FIELDNAME, effectivePerson.getDistinguishedName(), TaskCompleted.job_FIELDNAME,
					job, TaskCompleted.joinInquire_FIELDNAME, true);
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.out.println(taskCompleteds);
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			if (taskCompleteds.isEmpty()) {
				return result;
			}

			TaskCompleted taskCompleted = taskCompleteds.stream()
					.sorted(Comparator.comparing(TaskCompleted::getCreateTime).reversed()).findFirst()
					.orElseThrow(() -> new ExceptionAccessDenied(effectivePerson));

			List<WorkLog> workLogs = emc.listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, taskCompleted.getJob());

			List<WorkLog> down = WorkLog
					.upOrDownTo(workLogs,
							workLogs.stream().filter(
									o -> Objects.equals(o.getFromActivityToken(), taskCompleted.getActivityToken()))
									.collect(Collectors.toList()),
							false, ActivityType.manual)
					.stream().filter(o -> BooleanUtils.isNotTrue(o.getConnected())).collect(Collectors.toList());

			for (WorkLog o : down) {
				Work w = emc.firstEqual(Work.class, Work.activityToken_FIELDNAME, o.getFromActivityToken());
				if (null != w) {
					WoWork woWork = WoWork.copier.copy(w);
					woWork.getTaskList()
							.addAll(WoTask.copier.copy(emc.listEqual(Task.class, Task.work_FIELDNAME, w.getId())));
					wo.getWorkList().add(woWork);
				}
			}
			result.setData(wo);
		}
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -5007785846454720742L;

		@FieldDescribe("工作标识列表")
		private List<WoWork> workList = new ArrayList<>();

		public List<WoWork> getWorkList() {
			return workList;
		}

		public void setWorkList(List<WoWork> workList) {
			this.workList = workList;
		}

	}

	public static class WoWork extends GsonPropertyObject {

		private static final long serialVersionUID = -7504772027599791850L;

		private static final List<String> FIELDS = List.of(JpaObject.id_FIELDNAME, Work.title_FIELDNAME,
				Work.activity_FIELDNAME, Work.activityName_FIELDNAME, Work.splitting_FIELDNAME);

		static WrapCopier<Work, WoWork> copier = WrapCopierFactory.wo(Work.class, WoWork.class, FIELDS,
				JpaObject.FieldsInvisible);

		private String id;
		private String title;
		private String activity;
		private String activityName;
		private String splitValue;

		private ActivityType activityType;
		private List<WoTask> taskList = new ArrayList<>();

		public String getSplitValue() {
			return splitValue;
		}

		public void setSplitValue(String splitValue) {
			this.splitValue = splitValue;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getActivity() {
			return activity;
		}

		public void setActivity(String activity) {
			this.activity = activity;
		}

		public String getActivityName() {
			return activityName;
		}

		public void setActivityName(String activityName) {
			this.activityName = activityName;
		}

		public ActivityType getActivityType() {
			return activityType;
		}

		public void setActivityType(ActivityType activityType) {
			this.activityType = activityType;
		}

		public List<WoTask> getTaskList() {
			return taskList;
		}

		public void setTaskList(List<WoTask> taskList) {
			this.taskList = taskList;
		}

	}

	public static class WoTask extends GsonPropertyObject {

		private static final long serialVersionUID = 7081560152254505167L;

		private String id;
		private String person;

		private static final List<String> FIELDS = List.of(JpaObject.id_FIELDNAME, Task.person_FIELDNAME);

		static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class, FIELDS,
				JpaObject.FieldsInvisible);

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

	}

}