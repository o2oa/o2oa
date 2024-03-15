package com.x.processplatform.core.express.service.processing.jaxrs.record;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionWorkProcessingWi extends GsonPropertyObject {

	private static final long serialVersionUID = -6499166223522045961L;

	@FieldDescribe("记录类型.")
	private String recordType;
	@FieldDescribe("路由.")
	private String routeName;
	@FieldDescribe("办理意见.")
	private String opinion;
	@FieldDescribe("工作记录.")
	private String workLog;
	@FieldDescribe("处理人组织专有标识.")
	private String distinguishedName;
	@FieldDescribe("串号.")
	private String series;

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
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

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

}
