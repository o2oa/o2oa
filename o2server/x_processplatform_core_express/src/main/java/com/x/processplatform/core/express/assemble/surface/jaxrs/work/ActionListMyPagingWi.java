package com.x.processplatform.core.express.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionListMyPagingWi extends GsonPropertyObject {

	private static final long serialVersionUID = -4432862273663133127L;

	@FieldDescribe("应用标识.")
	@Schema(description = "应用标识.")
	private List<String> applicationList = new ArrayList<>();

	@FieldDescribe("流程标识.")
	@Schema(description = "流程标识.")
	private List<String> processList = new ArrayList<>();

	@FieldDescribe("是否查找同版本流程数据：true(默认查找)|false")
	@Schema(description = "是否查找同版本流程数据：true(默认查找)|false")
	private Boolean relateEditionProcess = true;

	@FieldDescribe("开始时间, 格式: yyyy-MM-dd HH:mm:ss")
	@Schema(description = "开始时间, 格式: yyyy-MM-dd HH:mm:ss")
	private String startTime;

	@FieldDescribe("结束时间, 格式: yyyy-MM-dd HH:mm:ss")
	@Schema(description = "结束时间, 格式: yyyy-MM-dd HH:mm:ss")
	private String endTime;

	@FieldDescribe("启动月份, 格式: yyyy-MM.")
	@Schema(description = "启动月份, 格式: yyyy-MM.")
	private List<String> startTimeMonthList = new ArrayList<>();

	@FieldDescribe("活动名称.")
	@Schema(description = "活动名称.")
	private List<String> activityNameList = new ArrayList<>();

	@FieldDescribe("是否已经经过人工节点,用于判断是否是草稿.在到达环节进行判断.")
	@Schema(description = "是否已经经过人工节点,用于判断是否是草稿.在到达环节进行判断.")
	private Boolean workThroughManual;

	@FieldDescribe("当前工作是否经过保存修改的操作,用于判断是否是默认生成的未经修改的.")
	@Schema(description = "当前工作是否经过保存修改的操作,用于判断是否是默认生成的未经修改的.")
	private Boolean dataChanged;

	@FieldDescribe("关键字.")
	@Schema(description = "关键字.")
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

	public Boolean getWorkThroughManual() {
		return workThroughManual;
	}

	public void setWorkThroughManual(Boolean workThroughManual) {
		this.workThroughManual = workThroughManual;
	}

	public Boolean getDataChanged() {
		return dataChanged;
	}

	public void setDataChanged(Boolean dataChanged) {
		this.dataChanged = dataChanged;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}