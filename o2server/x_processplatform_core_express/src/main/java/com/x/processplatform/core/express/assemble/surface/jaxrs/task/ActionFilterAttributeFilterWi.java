package com.x.processplatform.core.express.assemble.surface.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionFilterAttributeFilterWi extends GsonPropertyObject {

	private static final long serialVersionUID = -3260796933524976316L;

	@FieldDescribe("限制应用范围")
	@Schema(description = "限制应用范围列表")
	private List<String> applicationList = new ArrayList<>();

	@FieldDescribe("限制流程范围")
	@Schema(description = "限制流程范围列表")
	private List<String> processList = new ArrayList<>();

	@FieldDescribe("限制创建组织范围")
	@Schema(description = "限制创建组织范围列表")
	private List<String> creatorUnitList = new ArrayList<>();

	@FieldDescribe("限制创建月份范围")
	@Schema(description = "限制创建月份范围列表")
	private List<String> startTimeMonthList = new ArrayList<>();

	@FieldDescribe("限制活动名称范围")
	@Schema(description = "限制活动名称范围列表")
	private List<String> activityNameList = new ArrayList<>();

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

	public List<String> getCreatorUnitList() {
		return creatorUnitList;
	}

	public void setCreatorUnitList(List<String> creatorUnitList) {
		this.creatorUnitList = creatorUnitList;
	}

}