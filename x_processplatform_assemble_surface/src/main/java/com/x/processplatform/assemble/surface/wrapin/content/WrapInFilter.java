package com.x.processplatform.assemble.surface.wrapin.content;

import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkStatus;

@Wrap(TaskCompleted.class)
public class WrapInFilter extends GsonPropertyObject {

	private List<String> applicationList;

	private List<String> processList;

	private List<String> creatorCompanyList;

	private List<String> creatorDepartmentList;

	private List<String> completedTimeMonthList;

	private List<String> startTimeMonthList;

	private List<String> activityNameList;

	private List<WorkStatus> workStatusList;

	private String key;

	public List<String> getApplicationList() {
		return applicationList;
	}

	public void setApplicationList(List<String> applicationList) {
		this.applicationList = applicationList;
	}

	public List<String> getProcessList() {
		return processList;
	}

	public void setProcessList(List<String> processList) {
		this.processList = processList;
	}

	public List<String> getCreatorCompanyList() {
		return creatorCompanyList;
	}

	public void setCreatorCompanyList(List<String> creatorCompanyList) {
		this.creatorCompanyList = creatorCompanyList;
	}

	public List<String> getCreatorDepartmentList() {
		return creatorDepartmentList;
	}

	public void setCreatorDepartmentList(List<String> creatorDepartmentList) {
		this.creatorDepartmentList = creatorDepartmentList;
	}

	public List<String> getCompletedTimeMonthList() {
		return completedTimeMonthList;
	}

	public void setCompletedTimeMonthList(List<String> completedTimeMonthList) {
		this.completedTimeMonthList = completedTimeMonthList;
	}

	public List<String> getStartTimeMonthList() {
		return startTimeMonthList;
	}

	public void setStartTimeMonthList(List<String> startTimeMonthList) {
		this.startTimeMonthList = startTimeMonthList;
	}

	public List<String> getActivityNameList() {
		return activityNameList;
	}

	public void setActivityNameList(List<String> activityNameList) {
		this.activityNameList = activityNameList;
	}

	public List<WorkStatus> getWorkStatusList() {
		return workStatusList;
	}

	public void setWorkStatusList(List<WorkStatus> workStatusList) {
		this.workStatusList = workStatusList;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}