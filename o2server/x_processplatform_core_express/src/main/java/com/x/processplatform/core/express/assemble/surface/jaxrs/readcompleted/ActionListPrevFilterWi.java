package com.x.processplatform.core.express.assemble.surface.jaxrs.readcompleted;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionListPrevFilterWi extends GsonPropertyObject {

	private static final long serialVersionUID = -1890530580869003528L;

	@FieldDescribe("应用标识.")
	@Schema(description = "应用标识.")
	private List<String> applicationList;

	@FieldDescribe("流程标识.")
	@Schema(description = "流程标识.")
	private List<String> processList;

	@FieldDescribe("是否查找同版本流程数据：true(默认查找)|false.")
	@Schema(description = "是否查找同版本流程数据：true(默认查找)|false.")
	private Boolean relateEditionProcess = true;

	@FieldDescribe("创建人所在组织.")
	@Schema(description = "创建人所在组织.")
	private List<String> creatorUnitList;

	@FieldDescribe("开始时间月份.")
	@Schema(description = "开始时间月份.")
	private List<String> startTimeMonthList;

	@FieldDescribe("结束时间月份.")
	@Schema(description = "结束时间月份.")
	private List<String> completedTimeMonthList;

	@FieldDescribe("活动.")
	@Schema(description = "活动.")
	private List<String> activityNameList;

	@FieldDescribe("匹配关键字.")
	@Schema(description = "匹配关键字.")
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

	public Boolean getRelateEditionProcess() {
		return relateEditionProcess;
	}

	public void setRelateEditionProcess(Boolean relateEditionProcess) {
		this.relateEditionProcess = relateEditionProcess;
	}

	public List<String> getActivityNameList() {
		return activityNameList;
	}

	public void setActivityNameList(List<String> activityNameList) {
		this.activityNameList = activityNameList;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<String> getCreatorUnitList() {
		return creatorUnitList;
	}

	public void setCreatorUnitList(List<String> creatorUnitList) {
		this.creatorUnitList = creatorUnitList;
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

}