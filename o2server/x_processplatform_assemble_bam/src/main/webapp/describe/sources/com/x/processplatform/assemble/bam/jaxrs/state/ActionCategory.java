package com.x.processplatform.assemble.bam.jaxrs.state;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.bam.ThisApplication;

class ActionCategory extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionCategory.class);

	ActionResult<Wo> execute() throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		result.setData(ThisApplication.state.getCategory());
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("应用分类")
		private List<WoApplication> application = new ArrayList<>();

		@FieldDescribe("分类流程")
		private List<WoProcess> process = new ArrayList<>();

		@FieldDescribe("活动分类")
		private List<WoActivity> activity = new ArrayList<>();

		public List<WoApplication> getApplication() {
			return application;
		}

		public void setApplication(List<WoApplication> application) {
			this.application = application;
		}

		public List<WoProcess> getProcess() {
			return process;
		}

		public void setProcess(List<WoProcess> process) {
			this.process = process;
		}

		public List<WoActivity> getActivity() {
			return activity;
		}

		public void setActivity(List<WoActivity> activity) {
			this.activity = activity;
		}

	}

	public static class WoApplication extends GsonPropertyObject {

		@FieldDescribe("应用名称")
		private String name;

		@FieldDescribe("应用标识")
		private String value;

		@FieldDescribe("待办数量")
		private Long taskCount;

		@FieldDescribe("超时待办数量")
		private Long taskExpiredCount;

		@FieldDescribe("待办总时长")
		private Long taskDuration;

		@FieldDescribe("已完成待办数量")
		private Long taskCompletedCount;

		@FieldDescribe("已完成超时待办数量")
		private Long taskCompletedExpiredCount;

		@FieldDescribe("已完成待办总时长")
		private Long taskCompletedDuration;

		@FieldDescribe("工作数量")
		private Long workCount;

		@FieldDescribe("超时工作数量")
		private Long workExpiredCount;

		@FieldDescribe("工作总时长")
		private Long workDuration;

		@FieldDescribe("已完成工作数量")
		private Long workCompletedCount;

		@FieldDescribe("已完成超时工作数量")
		private Long workCompletedExpiredCount;

		@FieldDescribe("已完成工作总时长")
		private Long workCompletedDuration;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Long getTaskCount() {
			return taskCount;
		}

		public void setTaskCount(Long taskCount) {
			this.taskCount = taskCount;
		}

		public Long getTaskExpiredCount() {
			return taskExpiredCount;
		}

		public void setTaskExpiredCount(Long taskExpiredCount) {
			this.taskExpiredCount = taskExpiredCount;
		}

		public Long getTaskDuration() {
			return taskDuration;
		}

		public void setTaskDuration(Long taskDuration) {
			this.taskDuration = taskDuration;
		}

		public Long getTaskCompletedCount() {
			return taskCompletedCount;
		}

		public void setTaskCompletedCount(Long taskCompletedCount) {
			this.taskCompletedCount = taskCompletedCount;
		}

		public Long getTaskCompletedExpiredCount() {
			return taskCompletedExpiredCount;
		}

		public void setTaskCompletedExpiredCount(Long taskCompletedExpiredCount) {
			this.taskCompletedExpiredCount = taskCompletedExpiredCount;
		}

		public Long getTaskCompletedDuration() {
			return taskCompletedDuration;
		}

		public void setTaskCompletedDuration(Long taskCompletedDuration) {
			this.taskCompletedDuration = taskCompletedDuration;
		}

		public Long getWorkCount() {
			return workCount;
		}

		public void setWorkCount(Long workCount) {
			this.workCount = workCount;
		}

		public Long getWorkExpiredCount() {
			return workExpiredCount;
		}

		public void setWorkExpiredCount(Long workExpiredCount) {
			this.workExpiredCount = workExpiredCount;
		}

		public Long getWorkDuration() {
			return workDuration;
		}

		public void setWorkDuration(Long workDuration) {
			this.workDuration = workDuration;
		}

		public Long getWorkCompletedCount() {
			return workCompletedCount;
		}

		public void setWorkCompletedCount(Long workCompletedCount) {
			this.workCompletedCount = workCompletedCount;
		}

		public Long getWorkCompletedExpiredCount() {
			return workCompletedExpiredCount;
		}

		public void setWorkCompletedExpiredCount(Long workCompletedExpiredCount) {
			this.workCompletedExpiredCount = workCompletedExpiredCount;
		}

		public Long getWorkCompletedDuration() {
			return workCompletedDuration;
		}

		public void setWorkCompletedDuration(Long workCompletedDuration) {
			this.workCompletedDuration = workCompletedDuration;
		}

	}

	public static class WoProcess extends GsonPropertyObject {

		@FieldDescribe("流程名称")
		private String name;

		@FieldDescribe("流程标识")
		private String value;

		@FieldDescribe("应用名称")
		private String applicationName;

		@FieldDescribe("应用标识")
		private String applicationValue;

		@FieldDescribe("待办数量")
		private Long taskCount;

		@FieldDescribe("超时待办数量")
		private Long taskExpiredCount;

		@FieldDescribe("待办总耗时")
		private Long taskDuration;

		@FieldDescribe("已办数量")
		private Long taskCompletedCount;

		@FieldDescribe("超时已办数量")
		private Long taskCompletedExpiredCount;

		@FieldDescribe("已办总耗时")
		private Long taskCompletedDuration;

		@FieldDescribe("工作数量")
		private Long workCount;

		@FieldDescribe("超时工作数量")
		private Long workExpiredCount;

		@FieldDescribe("工作总耗时")
		private Long workDuration;

		@FieldDescribe("已完成工作数量")
		private Long workCompletedCount;

		@FieldDescribe("超时已完成工作数量")
		private Long workCompletedExpiredCount;

		@FieldDescribe("已完成工作总耗时")
		private Long workCompletedDuration;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getApplicationName() {
			return applicationName;
		}

		public void setApplicationName(String applicationName) {
			this.applicationName = applicationName;
		}

		public String getApplicationValue() {
			return applicationValue;
		}

		public void setApplicationValue(String applicationValue) {
			this.applicationValue = applicationValue;
		}

		public Long getTaskCount() {
			return taskCount;
		}

		public void setTaskCount(Long taskCount) {
			this.taskCount = taskCount;
		}

		public Long getTaskExpiredCount() {
			return taskExpiredCount;
		}

		public void setTaskExpiredCount(Long taskExpiredCount) {
			this.taskExpiredCount = taskExpiredCount;
		}

		public Long getTaskDuration() {
			return taskDuration;
		}

		public void setTaskDuration(Long taskDuration) {
			this.taskDuration = taskDuration;
		}

		public Long getTaskCompletedCount() {
			return taskCompletedCount;
		}

		public void setTaskCompletedCount(Long taskCompletedCount) {
			this.taskCompletedCount = taskCompletedCount;
		}

		public Long getTaskCompletedExpiredCount() {
			return taskCompletedExpiredCount;
		}

		public void setTaskCompletedExpiredCount(Long taskCompletedExpiredCount) {
			this.taskCompletedExpiredCount = taskCompletedExpiredCount;
		}

		public Long getTaskCompletedDuration() {
			return taskCompletedDuration;
		}

		public void setTaskCompletedDuration(Long taskCompletedDuration) {
			this.taskCompletedDuration = taskCompletedDuration;
		}

		public Long getWorkCount() {
			return workCount;
		}

		public void setWorkCount(Long workCount) {
			this.workCount = workCount;
		}

		public Long getWorkExpiredCount() {
			return workExpiredCount;
		}

		public void setWorkExpiredCount(Long workExpiredCount) {
			this.workExpiredCount = workExpiredCount;
		}

		public Long getWorkDuration() {
			return workDuration;
		}

		public void setWorkDuration(Long workDuration) {
			this.workDuration = workDuration;
		}

		public Long getWorkCompletedCount() {
			return workCompletedCount;
		}

		public void setWorkCompletedCount(Long workCompletedCount) {
			this.workCompletedCount = workCompletedCount;
		}

		public Long getWorkCompletedExpiredCount() {
			return workCompletedExpiredCount;
		}

		public void setWorkCompletedExpiredCount(Long workCompletedExpiredCount) {
			this.workCompletedExpiredCount = workCompletedExpiredCount;
		}

		public Long getWorkCompletedDuration() {
			return workCompletedDuration;
		}

		public void setWorkCompletedDuration(Long workCompletedDuration) {
			this.workCompletedDuration = workCompletedDuration;
		}

	}

	public static class WoActivity extends GsonPropertyObject {
		@FieldDescribe("流程名称")
		private String name;

		@FieldDescribe("流程标识")
		private String value;

		@FieldDescribe("应用名称")
		private String applicationName;

		@FieldDescribe("应用标识")
		private String applicationValue;

		@FieldDescribe("流程名称")
		private String processName;

		@FieldDescribe("流程标识")
		private String processValue;

		@FieldDescribe("待办数量")
		private Long taskCount;

		@FieldDescribe("超时待办数量")
		private Long taskExpiredCount;

		@FieldDescribe("待办总耗时")
		private Long taskDuration;

		@FieldDescribe("已办数量")
		private Long taskCompletedCount;

		@FieldDescribe("超时已办数量")
		private Long taskCompletedExpiredCount;

		@FieldDescribe("已办总耗时")
		private Long taskCompletedDuration;

		@FieldDescribe("工作数量")
		private Long workCount;

		@FieldDescribe("超时工作数量")
		private Long workExpiredCount;

		@FieldDescribe("工作总耗时")
		private Long workDuration;

		// @FieldDescribe("已完成工作数量")
		// private Long workCompletedCount;
		//
		// @FieldDescribe("超时已完成工作数量")
		// private Long workCompletedExpiredCount;
		//
		// @FieldDescribe("已完成工作总耗时")
		// private Long workCompletedDuration;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getApplicationName() {
			return applicationName;
		}

		public void setApplicationName(String applicationName) {
			this.applicationName = applicationName;
		}

		public String getApplicationValue() {
			return applicationValue;
		}

		public void setApplicationValue(String applicationValue) {
			this.applicationValue = applicationValue;
		}

		public Long getTaskCount() {
			return taskCount;
		}

		public void setTaskCount(Long taskCount) {
			this.taskCount = taskCount;
		}

		public Long getTaskExpiredCount() {
			return taskExpiredCount;
		}

		public void setTaskExpiredCount(Long taskExpiredCount) {
			this.taskExpiredCount = taskExpiredCount;
		}

		public Long getTaskDuration() {
			return taskDuration;
		}

		public void setTaskDuration(Long taskDuration) {
			this.taskDuration = taskDuration;
		}

		public Long getTaskCompletedCount() {
			return taskCompletedCount;
		}

		public void setTaskCompletedCount(Long taskCompletedCount) {
			this.taskCompletedCount = taskCompletedCount;
		}

		public Long getTaskCompletedExpiredCount() {
			return taskCompletedExpiredCount;
		}

		public void setTaskCompletedExpiredCount(Long taskCompletedExpiredCount) {
			this.taskCompletedExpiredCount = taskCompletedExpiredCount;
		}

		public Long getTaskCompletedDuration() {
			return taskCompletedDuration;
		}

		public void setTaskCompletedDuration(Long taskCompletedDuration) {
			this.taskCompletedDuration = taskCompletedDuration;
		}

		public Long getWorkCount() {
			return workCount;
		}

		public void setWorkCount(Long workCount) {
			this.workCount = workCount;
		}

		public Long getWorkExpiredCount() {
			return workExpiredCount;
		}

		public void setWorkExpiredCount(Long workExpiredCount) {
			this.workExpiredCount = workExpiredCount;
		}

		public Long getWorkDuration() {
			return workDuration;
		}

		public void setWorkDuration(Long workDuration) {
			this.workDuration = workDuration;
		}

		public String getProcessName() {
			return processName;
		}

		public void setProcessName(String processName) {
			this.processName = processName;
		}

		public String getProcessValue() {
			return processValue;
		}

		public void setProcessValue(String processValue) {
			this.processValue = processValue;
		}

	}

}
