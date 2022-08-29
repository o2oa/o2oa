package com.x.processplatform.core.express.assemble.surface.jaxrs.work;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.content.WorkStatus;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionListNextCreatorWithCurrentFilterWi extends GsonPropertyObject {

	private static final long serialVersionUID = -5667997938739822509L;

	@FieldDescribe("流程标识.")
	@Schema(description = "流程标识.")
	private List<String> processList;

	@FieldDescribe("创建工作人员身份所属组织.")
	@Schema(description = "创建工作人员身份所属组织.")
	private List<String> creatorUnitList;

	@FieldDescribe("创建工作启动月份,格式为: yyyy-MM.")
	@Schema(description = "创建工作启动月份,格式为: yyyy-MM.")
	private List<String> startTimeMonthList;

	@FieldDescribe("活动名称.")
	@Schema(description = "活动名称.")
	private List<String> activityNameList;

	@FieldDescribe("工作状态.")
	@Schema(description = "工作状态.")
	private List<WorkStatus> workStatusList;

	@FieldDescribe("关键字.")
	@Schema(description = "关键字.")
	private String key;

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

	public List<String> getCreatorUnitList() {
		return creatorUnitList;
	}

	public void setCreatorUnitList(List<String> creatorUnitList) {
		this.creatorUnitList = creatorUnitList;
	}

}