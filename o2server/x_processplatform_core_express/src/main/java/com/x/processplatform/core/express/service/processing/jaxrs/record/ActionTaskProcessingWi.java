package com.x.processplatform.core.express.service.processing.jaxrs.record;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionTaskProcessingWi extends GsonPropertyObject {

	private static final long serialVersionUID = -6499166223522045961L;

	@FieldDescribe("记录类型.")
	private String recordType;
	@FieldDescribe("工作记录.")
	private String workLog;
	@FieldDescribe("已办.")
	private String taskCompleted;
	@FieldDescribe("串号.")
	private String series;

	public String getTaskCompleted() {
		return taskCompleted;
	}

	public void setTaskCompleted(String taskCompleted) {
		this.taskCompleted = taskCompleted;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getWorkLog() {
		return workLog;
	}

	public void setWorkLog(String workLog) {
		this.workLog = workLog;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

}
