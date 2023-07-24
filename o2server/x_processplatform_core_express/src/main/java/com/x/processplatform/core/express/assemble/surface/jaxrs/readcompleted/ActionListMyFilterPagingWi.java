package com.x.processplatform.core.express.assemble.surface.jaxrs.readcompleted;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionListMyFilterPagingWi extends GsonPropertyObject {

	private static final long serialVersionUID = 6923843166913352622L;

	@FieldDescribe("应用标识.")
	@Schema(description = "应用标识.")
	private List<String> applicationList;

	@FieldDescribe("流程标识.")
	@Schema(description = "流程标识.")
	private List<String> processList;

	@FieldDescribe("是否查找同版本流程数据：true(默认查找)|false.")
	@Schema(description = "是否查找同版本流程数据：true(默认查找)|false.")
	private Boolean relateEditionProcess = true;

	@FieldDescribe("开始时间,格式为:yyyy-MM-dd HH:mm:ss")
	@Schema(description = "开始时间,格式为:yyyy-MM-dd HH:mm:ss")
	private String startTime;

	@FieldDescribe("结束时间,格式为:yyyy-MM-dd HH:mm:ss")
	@Schema(description = "结束时间,格式为:yyyy-MM-dd HH:mm:ss")
	private String endTime;

	@FieldDescribe("活动名称.")
	@Schema(description = "活动名称.")
	private List<String> activityNameList;

	@FieldDescribe("创建组织.")
	@Schema(description = "创建组织.")
	private List<String> creatorUnitList;

	@FieldDescribe("开始时间(年月).")
	@Schema(description = "开始时间(年月).")
	private List<String> startTimeMonthList;

	@FieldDescribe("结束时间(年月).")
	@Schema(description = "结束时间(年月).")
	private List<String> completedTimeMonthList;

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

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

}