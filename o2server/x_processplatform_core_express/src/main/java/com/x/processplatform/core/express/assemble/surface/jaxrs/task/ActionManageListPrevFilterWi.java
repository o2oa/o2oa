package com.x.processplatform.core.express.assemble.surface.jaxrs.task;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionManageListPrevFilterWi extends GsonPropertyObject {

	private static final long serialVersionUID = -3378981852416873278L;

	@FieldDescribe("应用标识.")
	@Schema(description = "应用标识.")
	private List<String> applicationList;

	@FieldDescribe("流程标识.")
	@Schema(description = "流程标识.")
	private List<String> processList;

	@FieldDescribe("人员标识.")
	@Schema(description = "人员标识.")
	private List<String> credentialList;

	@FieldDescribe("活动名称.")
	@Schema(description = "活动名称.")
	private List<String> activityNameList;

	@FieldDescribe("创建工作身份所属组织.")
	@Schema(description = "创建工作身份所属组织.")
	private List<String> creatorUnitList;

	@FieldDescribe("开始年月,格式为文本格式 yyyy-MM")
	@Schema(description = "开始年月,格式为文本格式 yyyy-MM")
	private List<String> startTimeMonthList;

	@FieldDescribe("搜索关键字,搜索范围为:标题,意见,文号,创建人,创建部门.")
	@Schema(description = "搜索关键字,搜索范围为:标题,意见,文号,创建人,创建部门.")
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

	public List<String> getCredentialList() {
		return credentialList;
	}

	public void setCredentialList(List<String> credentialList) {
		this.credentialList = credentialList;
	}
}