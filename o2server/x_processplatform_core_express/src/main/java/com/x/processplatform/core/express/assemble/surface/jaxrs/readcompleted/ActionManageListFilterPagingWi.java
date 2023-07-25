package com.x.processplatform.core.express.assemble.surface.jaxrs.readcompleted;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionManageListFilterPagingWi extends GsonPropertyObject {

	private static final long serialVersionUID = 5271416306646803042L;

	@FieldDescribe("应用标识.")
	@Schema(description = "应用标识.")
	private List<String> applicationList;

	@FieldDescribe("流程标识.")
	@Schema(description = "流程标识.")
	private List<String> processList;

	@FieldDescribe("是否查找同版本流程数据：true(默认查找)|false.")
	@Schema(description = "是否查找同版本流程数据：true(默认查找)|false.")
	private Boolean relateEditionProcess = true;

	@FieldDescribe("开始时间yyyy-MM-dd HH:mm:ss.")
	@Schema(description = "开始时间yyyy-MM-dd HH:mm:ss.")
	private String startTime;

	@FieldDescribe("结束时间yyyy-MM-dd HH:mm:ss.")
	@Schema(description = "结束时间yyyy-MM-dd HH:mm:ss.")
	private String endTime;

	@FieldDescribe("人员.")
	@Schema(description = "人员.")
	private List<String> credentialList;

	@FieldDescribe("活动名称.")
	@Schema(description = "活动名称.")
	private List<String> activityNameList;

	@FieldDescribe("创建组织.")
	@Schema(description = "创建组织.")
	private List<String> creatorUnitList;

	@FieldDescribe("工作标识.")
	@Schema(description = "工作标识.")
	private List<String> workList;

	@FieldDescribe("任务标识.")
	@Schema(description = "任务标识.")
	private List<String> jobList;

	@FieldDescribe("开始时期(年月).")
	@Schema(description = "开始时期(年月).")
	private List<String> startTimeMonthList;

	@FieldDescribe("匹配关键字.")
	@Schema(description = "匹配关键字.")
	private String key;

	@FieldDescribe("当前待办人.")
	@Schema(description = "当前待办人.")
	private String person;

	@FieldDescribe("业务数据String值01.")
	@Schema(description = "业务数据String值01.")
	private String stringValue01;

	@FieldDescribe("业务数据String值02.")
	@Schema(description = "业务数据String值02.")
	private String stringValue02;

	@FieldDescribe("业务数据String值03.")
	@Schema(description = "业务数据String值03.")
	private String stringValue03;

	@FieldDescribe("业务数据String值04.")
	@Schema(description = "业务数据String值04.")
	private String stringValue04;

	@FieldDescribe("业务数据String值05.")
	@Schema(description = "业务数据String值05.")
	private String stringValue05;

	@FieldDescribe("业务数据String值06.")
	@Schema(description = "业务数据String值06.")
	private String stringValue06;

	@FieldDescribe("业务数据String值07.")
	@Schema(description = "业务数据String值07.")
	private String stringValue07;

	@FieldDescribe("业务数据String值08.")
	@Schema(description = "业务数据String值08.")
	private String stringValue08;

	@FieldDescribe("业务数据String值09.")
	@Schema(description = "业务数据String值09.")
	private String stringValue09;

	@FieldDescribe("业务数据String值10.")
	@Schema(description = "业务数据String值10.")
	private String stringValue10;

	public List<String> getApplicationList() {
		return applicationList;
	}

	public String getPerson() {
		return person;
	}

	public String getStringValue01() {
		return stringValue01;
	}

	public String getStringValue02() {
		return stringValue02;
	}

	public String getStringValue03() {
		return stringValue03;
	}

	public String getStringValue04() {
		return stringValue04;
	}

	public String getStringValue05() {
		return stringValue05;
	}

	public String getStringValue06() {
		return stringValue06;
	}

	public String getStringValue07() {
		return stringValue07;
	}

	public String getStringValue08() {
		return stringValue08;
	}

	public String getStringValue09() {
		return stringValue09;
	}

	public String getStringValue10() {
		return stringValue10;
	}

	public void setStringValue01(String stringValue01) {
		this.stringValue01 = stringValue01;
	}

	public void setStringValue02(String stringValue02) {
		this.stringValue02 = stringValue02;
	}

	public void setStringValue03(String stringValue03) {
		this.stringValue03 = stringValue03;
	}

	public void setStringValue04(String stringValue04) {
		this.stringValue04 = stringValue04;
	}

	public void setStringValue05(String stringValue05) {
		this.stringValue05 = stringValue05;
	}

	public void setStringValue06(String stringValue06) {
		this.stringValue06 = stringValue06;
	}

	public void setStringValue07(String stringValue07) {
		this.stringValue07 = stringValue07;
	}

	public void setStringValue08(String stringValue08) {
		this.stringValue08 = stringValue08;
	}

	public void setStringValue09(String stringValue09) {
		this.stringValue09 = stringValue09;
	}

	public void setStringValue10(String stringValue10) {
		this.stringValue10 = stringValue10;
	}

	public void setPerson(String person) {
		this.person = person;
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

	public List<String> getCredentialList() {
		return credentialList;
	}

	public void setCredentialList(List<String> credentialList) {
		this.credentialList = credentialList;
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

	public List<String> getWorkList() {
		return workList;
	}

	public void setWorkList(List<String> workList) {
		this.workList = workList;
	}

	public List<String> getJobList() {
		return jobList;
	}

	public void setJobList(List<String> jobList) {
		this.jobList = jobList;
	}
}