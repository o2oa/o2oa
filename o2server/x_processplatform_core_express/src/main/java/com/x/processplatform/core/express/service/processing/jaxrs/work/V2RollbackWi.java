package com.x.processplatform.core.express.service.processing.jaxrs.work;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class V2RollbackWi extends GsonPropertyObject {

	private static final long serialVersionUID = 4148950553127792266L;

	@FieldDescribe("工作日志")
	private String workLog;

	@FieldDescribe("已完成工作处理人.")
	private List<String> taskCompletedIdentityList;

	@FieldDescribe("是否尝试流转")
	private Boolean processing;

	public Boolean getProcessing() {
		return BooleanUtils.isTrue(processing);
	}

	public String getWorkLog() {
		return workLog;
	}

	public void setWorkLog(String workLog) {
		this.workLog = workLog;
	}

	public void setProcessing(Boolean processing) {
		this.processing = processing;
	}

	public List<String> getTaskCompletedIdentityList() {
		return taskCompletedIdentityList;
	}

	public void setTaskCompletedIdentityList(List<String> taskCompletedIdentityList) {
		this.taskCompletedIdentityList = taskCompletedIdentityList;
	}

}