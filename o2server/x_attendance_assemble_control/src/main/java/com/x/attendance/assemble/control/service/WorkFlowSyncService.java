package com.x.attendance.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.attendance.assemble.control.ThisApplication;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;

public class WorkFlowSyncService {

	public static WoWorkOrCompletedComplex getWorkComplex( String workId ) throws Exception {
		String serviceUri = "work/v2/workorworkcompleted/" + workId;
		ActionResponse resp = ThisApplication.context().applications().getQuery(
				x_processplatform_assemble_surface.class, serviceUri
		);
		WoWorkOrCompletedComplex wo = resp.getData( WoWorkOrCompletedComplex.class );
		return wo;
	}

	public static WoWorkOrCompletedComplex getWorkCompletedComplex( String workId ) throws Exception {
		String serviceUri = "workcompleted/" + workId;
		ActionResponse resp = ThisApplication.context().applications().getQuery(
				x_processplatform_assemble_surface.class, serviceUri
		);
		WoWorkOrCompletedComplex wo = resp.getData( WoWorkOrCompletedComplex.class );
		return wo;
	}

	public static class WoWorkOrCompletedComplex extends GsonPropertyObject {

		@FieldDescribe("活动节点")
		private WoActivity activity;

		@FieldDescribe("工作")
		private WoWork work;

		@FieldDescribe("待办对象列表")
		private List<WoTask> taskList = new ArrayList<>();

		@FieldDescribe("工作日志对象")
		private List<WoWorkLog> workLogList = new ArrayList<>();

		public WoActivity getActivity() {
			return activity;
		}

		public WoWork getWork() {
			return work;
		}

		public List<WoWorkLog> getWorkLogList() {
			return workLogList;
		}

		public void setActivity(WoActivity activity) {
			this.activity = activity;
		}

		public void setWork(WoWork work) {
			this.work = work;
		}

		public void setWorkLogList(List<WoWorkLog> workLogList) {
			this.workLogList = workLogList;
		}

		public List<WoTask> getTaskList() {
			return taskList;
		}

		public void setTaskList(List<WoTask> taskList) {
			this.taskList = taskList;
		}
	}

	public static class WoActivity extends GsonPropertyObject {

		private String id;

		private String name;

		private String description;

		private String alias;

		private String position;

		private ActivityType activityType;

		private String resetRange;

		private Integer resetCount;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public String getPosition() {
			return position;
		}

		public void setPosition(String position) {
			this.position = position;
		}

		public ActivityType getActivityType() {
			return activityType;
		}

		public void setActivityType(ActivityType activityType) {
			this.activityType = activityType;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public Integer getResetCount() {
			return resetCount;
		}

		public void setResetCount(Integer resetCount) {
			this.resetCount = resetCount;
		}

		public String getResetRange() {
			return resetRange;
		}

		public void setResetRange(String resetRange) {
			this.resetRange = resetRange;
		}

	}

	public static class WoWork extends Work {

		private static final long serialVersionUID = 3269592171662996253L;

		static WrapCopier<Work, WoWork> copier = WrapCopierFactory.wo(Work.class, WoWork.class, null,
				JpaObject.FieldsInvisible);
	}

	public static class WoWorkLog extends WorkLog {

		private static final long serialVersionUID = 1307569946729101786L;

		public static WrapCopier<WorkLog, WoWorkLog> copier = WrapCopierFactory.wo(WorkLog.class, WoWorkLog.class, null,
				JpaObject.FieldsInvisible);

		private List<WoTaskCompleted> taskCompletedList;

		private List<WoTask> taskList;

		private Integer currentTaskIndex;

		public Integer getCurrentTaskIndex() {
			return currentTaskIndex;
		}

		public void setCurrentTaskIndex(Integer currentTaskIndex) {
			this.currentTaskIndex = currentTaskIndex;
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

	}

	public static class WoTask extends Task {

		private static final long serialVersionUID = 2279846765261247910L;

		public static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class WoRead extends Read {

		private static final long serialVersionUID = -8067704098385000667L;

		public static WrapCopier<Read, WoRead> copier = WrapCopierFactory.wo(Read.class, WoRead.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class WoTaskCompleted extends TaskCompleted {

		private static final long serialVersionUID = -7253999118308715077L;

		public static WrapCopier<TaskCompleted, WoTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
				WoTaskCompleted.class, null, JpaObject.FieldsInvisible);
		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}



}
