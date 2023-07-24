package com.x.processplatform.core.express.assemble.surface.jaxrs.draft;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionListMyPagingWi extends GsonPropertyObject {

	private static final long serialVersionUID = -7337529488065203886L;

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

	@FieldDescribe("创建组织.")
	@Schema(description = "创建组织.")
	private List<String> creatorUnitList;

	@FieldDescribe("标题.")
	@Schema(description = "标题.")
	private String title;

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

	public List<String> getCreatorUnitList() {
		return creatorUnitList;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}