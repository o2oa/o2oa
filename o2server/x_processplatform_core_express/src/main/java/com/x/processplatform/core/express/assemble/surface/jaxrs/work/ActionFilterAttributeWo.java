package com.x.processplatform.core.express.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionFilterAttributeWo extends GsonPropertyObject {

	private static final long serialVersionUID = 1206560608003793762L;

	@FieldDescribe("可选择的流程")
	private List<NameValueCountPair> processList = new ArrayList<>();

	@FieldDescribe("可选择的组织")
	private List<NameValueCountPair> creatorUnitList = new ArrayList<>();

	@FieldDescribe("可选择的开始月份")
	private List<NameValueCountPair> startTimeMonthList = new ArrayList<>();

	@FieldDescribe("可选择的活动节点")
	private List<NameValueCountPair> activityNameList = new ArrayList<>();

	@FieldDescribe("可选择的工作状态")
	@FieldTypeDescribe(fieldType = "enum", fieldValue = "start|processing|hanging", fieldTypeName = "com.x.processplatform.core.entity.content.WorkStatus")
	private List<NameValueCountPair> workStatusList = new ArrayList<>();

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

	public List<NameValueCountPair> getStartTimeMonthList() {
		return startTimeMonthList;
	}

	public void setStartTimeMonthList(List<NameValueCountPair> startTimeMonthList) {
		this.startTimeMonthList = startTimeMonthList;
	}

	public List<NameValueCountPair> getActivityNameList() {
		return activityNameList;
	}

	public void setActivityNameList(List<NameValueCountPair> activityNameList) {
		this.activityNameList = activityNameList;
	}

	public List<NameValueCountPair> getWorkStatusList() {
		return workStatusList;
	}

	public void setWorkStatusList(List<NameValueCountPair> workStatusList) {
		this.workStatusList = workStatusList;
	}

}