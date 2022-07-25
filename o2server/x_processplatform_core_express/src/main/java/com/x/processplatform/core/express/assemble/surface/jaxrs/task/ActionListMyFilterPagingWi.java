package com.x.processplatform.core.express.assemble.surface.jaxrs.task;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionListMyFilterPagingWi extends GsonPropertyObject {

	private static final long serialVersionUID = 4869280182868835401L;

	@FieldDescribe("应用标识.")
	@Schema(description = "应用标识.")
	private List<String> applicationList;

	@FieldDescribe("流程标识.")
	@Schema(description = "流程标识.")
	private List<String> processList;

	@FieldDescribe("是否查找同版本流程数据：true(默认查找)|false")
	@Schema(description = "是否查找同版本流程数据：true(默认查找)|false")
	private Boolean relateEditionProcess = true;

	@FieldDescribe("是否排除草稿待办：false(默认不查找)|true")
	@Schema(description = "是否排除草稿待办：false(默认不查找)|true")
	private Boolean isExcludeDraft;

	@FieldDescribe("开始时间,格式为: yyyy-MM-dd HH:mm:ss")
	@Schema(description = "开始时间,格式为: yyyy-MM-dd HH:mm:ss")
	private String startTime;

	@FieldDescribe("结束时间,格式为: yyyy-MM-dd HH:mm:ss")
	@Schema(description = "结束时间,格式为: yyyy-MM-dd HH:mm:ss")
	private String endTime;

	@FieldDescribe("活动名称")
	@Schema(description = "活动名称")
	private List<String> activityNameList;

	@FieldDescribe("创建工作身份所属组织.")
	@Schema(description = "创建工作身份所属组织.")
	private List<String> creatorUnitList;

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

	public List<String> getCreatorUnitList() {
		return ListTools.trim(creatorUnitList, true, true);
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

	public Boolean getExcludeDraft() {
		return isExcludeDraft;
	}

	public void setExcludeDraft(Boolean excludeDraft) {
		isExcludeDraft = excludeDraft;
	}
}
