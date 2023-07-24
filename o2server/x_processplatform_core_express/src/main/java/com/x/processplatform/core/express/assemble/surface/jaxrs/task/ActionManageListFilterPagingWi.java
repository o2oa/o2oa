package com.x.processplatform.core.express.assemble.surface.jaxrs.task;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionManageListFilterPagingWi extends GsonPropertyObject {

	private static final long serialVersionUID = 3548558189684378598L;

	@FieldDescribe("应用标识.")
	@Schema(description = "应用标识.")
	private List<String> applicationList;

	@FieldDescribe("流程标识.")
	@Schema(description = "流程标识.")
	private List<String> processList;

	@FieldDescribe("是否查找同版本流程待办：true(默认查找)|false.")
	@Schema(description = "是否查找同版本流程待办：true(默认查找)|false.")
	private Boolean relateEditionProcess = true;

	@FieldDescribe("是否排除草稿待办：false(默认不排除)|true.")
	@Schema(description = "是否排除草稿待办：false(默认不排除)|true.")
	private Boolean isExcludeDraft;

	@FieldDescribe("开始时间,格式为:yyyy-MM-dd HH:mm:ss.")
	@Schema(description = "开始时间,格式为:yyyy-MM-dd HH:mm:ss.")
	private String startTime;

	@FieldDescribe("结束时间,格式为:yyyy-MM-dd HH:mm:ss.")
	@Schema(description = "结束时间,格式为:yyyy-MM-dd HH:mm:ss.")
	private String endTime;

	@FieldDescribe("人员标识.")
	@Schema(description = "人员标识.")
	private List<String> credentialList;

	@FieldDescribe("活动名称.")
	@Schema(description = "活动名称.")
	private List<String> activityNameList;

	@FieldDescribe("创建工作身份所属组织.")
	@Schema(description = "创建工作身份所属组织.")
	private List<String> creatorUnitList;

	@FieldDescribe("工作标识.")
	@Schema(description = "工作标识.")
	private List<String> workList;

	@FieldDescribe("任务标识.")
	@Schema(description = "任务标识.")
	private List<String> jobList;

	@FieldDescribe("开始年月,格式为文本格式 yyyy-MM")
	@Schema(description = "开始年月,格式为文本格式 yyyy-MM")
	private List<String> startTimeMonthList;

	@FieldDescribe("时效超时时间（0表示所有已超时的、1表示超时1小时以上的、2、3...）")
	@Schema(description = "时效超时时间（0表示所有已超时的、1表示超时1小时以上的、2、3...）")
	private String expireTime;

	@FieldDescribe("催办超时时间（0表示所有已超时的、1表示超时1小时以上的、2、3...）")
	@Schema(description = "催办超时时间（0表示所有已超时的、1表示超时1小时以上的、2、3...）")
	private String urgeTime;

	@FieldDescribe("搜索关键字,搜索范围为:标题,意见,文号,创建人,创建部门.")
	@Schema(description = "搜索关键字,搜索范围为:标题,意见,文号,创建人,创建部门.")
	private String key;

	@FieldDescribe("业务数据String值01.")
	@Schema(description = "业务数据String值01.")
	private String stringValue01;
	@FieldDescribe("业务数据String值02")
	@Schema(description = "业务数据String值02.")
	private String stringValue02;
	@FieldDescribe("业务数据String值03")
	@Schema(description = "业务数据String值03.")
	private String stringValue03;
	@FieldDescribe("业务数据String值04")
	@Schema(description = "业务数据String值04.")
	private String stringValue04;
	@FieldDescribe("业务数据String值05")
	@Schema(description = "业务数据String值05.")
	private String stringValue05;
	@FieldDescribe("业务数据String值06")
	@Schema(description = "业务数据String值06.")
	private String stringValue06;
	@FieldDescribe("业务数据String值07")
	@Schema(description = "业务数据String值07.")
	private String stringValue07;
	@FieldDescribe("业务数据String值08")
	@Schema(description = "业务数据String值08.")
	private String stringValue08;
	@FieldDescribe("业务数据String值09")
	@Schema(description = "业务数据String值09.")
	private String stringValue09;
	@FieldDescribe("业务数据String值10")
	@Schema(description = "业务数据String值10.")
	private String stringValue10;

	public List<String> getApplicationList() {
		return ListTools.trim(applicationList, true, true);
	}

	public List<String> getProcessList() {
		return ListTools.trim(processList, true, true);
	}

	public List<String> getStartTimeMonthList() {
		return ListTools.trim(startTimeMonthList, true, true);
	}

	public List<String> getActivityNameList() {
		return ListTools.trim(activityNameList, true, true);
	}

	public List<String> getJobList() {
		return ListTools.trim(jobList, true, true);
	}

	public List<String> getWorkList() {
		return ListTools.trim(workList, true, true);
	}

	public List<String> getCredentialList() {
		return ListTools.trim(credentialList, true, true);
	}

	public List<String> getCreatorUnitList() {
		return ListTools.trim(creatorUnitList, true, true);
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

	public void setApplicationList(List<String> applicationList) {
		this.applicationList = applicationList;
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

	public void setStartTimeMonthList(List<String> startTimeMonthList) {
		this.startTimeMonthList = startTimeMonthList;
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

	public void setCreatorUnitList(List<String> creatorUnitList) {
		this.creatorUnitList = creatorUnitList;
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

	public String getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(String expireTime) {
		this.expireTime = expireTime;
	}

	public String getUrgeTime() {
		return urgeTime;
	}

	public void setUrgeTime(String urgeTime) {
		this.urgeTime = urgeTime;
	}

	public void setWorkList(List<String> workList) {
		this.workList = workList;
	}

	public void setJobList(List<String> jobList) {
		this.jobList = jobList;
	}

	public Boolean getExcludeDraft() {
		return isExcludeDraft;
	}

	public void setExcludeDraft(Boolean excludeDraft) {
		isExcludeDraft = excludeDraft;
	}
}