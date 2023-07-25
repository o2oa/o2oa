package com.x.processplatform.core.express.assemble.surface.jaxrs.readcompleted;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionFilterAttributeWo extends GsonPropertyObject {

	private static final long serialVersionUID = 1632781666272985480L;

	@FieldDescribe("可选应用标识.")
	@Schema(description = "可选应用标识.")
	private List<NameValueCountPair> applicationList = new ArrayList<>();
	@FieldDescribe("可选流程标识.")
	@Schema(description = "可选流程标识.")
	private List<NameValueCountPair> processList = new ArrayList<>();
	@FieldDescribe("可选组织标识.")
	@Schema(description = "可选组织标识.")
	private List<NameValueCountPair> creatorUnitList = new ArrayList<>();
	@FieldDescribe("可选创建时间范围.")
	@Schema(description = "可选创建时间范围.")
	private List<NameValueCountPair> startTimeMonthList = new ArrayList<>();
	@FieldDescribe("可选结束时间范围.")
	@Schema(description = "可选结束时间范围.")
	private List<NameValueCountPair> completedTimeMonthList = new ArrayList<>();
	@FieldDescribe("可选活动范围.")
	@Schema(description = "可选活动范围.")
	private List<NameValueCountPair> activityNameList = new ArrayList<>();
	@FieldDescribe("可选择的完成状态.")
	@Schema(description = "可选择的完成状态.")
	private List<NameValueCountPair> completedList = new ArrayList<>();

	public List<NameValueCountPair> getApplicationList() {
		return applicationList;
	}

	public void setApplicationList(List<NameValueCountPair> applicationList) {
		this.applicationList = applicationList;
	}

	public List<NameValueCountPair> getProcessList() {
		return processList;
	}

	public void setProcessList(List<NameValueCountPair> processList) {
		this.processList = processList;
	}

	public List<NameValueCountPair> getCreatorUnitList() {
		return creatorUnitList;
	}

	public void setCreatorUnitList(List<NameValueCountPair> creatorUnitList) {
		this.creatorUnitList = creatorUnitList;
	}

	public List<NameValueCountPair> getActivityNameList() {
		return activityNameList;
	}

	public void setActivityNameList(List<NameValueCountPair> activityNameList) {
		this.activityNameList = activityNameList;
	}

	public List<NameValueCountPair> getCompletedTimeMonthList() {
		return completedTimeMonthList;
	}

	public void setCompletedTimeMonthList(List<NameValueCountPair> completedTimeMonthList) {
		this.completedTimeMonthList = completedTimeMonthList;
	}

	public List<NameValueCountPair> getStartTimeMonthList() {
		return startTimeMonthList;
	}

	public void setStartTimeMonthList(List<NameValueCountPair> startTimeMonthList) {
		this.startTimeMonthList = startTimeMonthList;
	}

	public List<NameValueCountPair> getCompletedList() {
		return completedList;
	}

	public void setCompletedList(List<NameValueCountPair> completedList) {
		this.completedList = completedList;
	}

}