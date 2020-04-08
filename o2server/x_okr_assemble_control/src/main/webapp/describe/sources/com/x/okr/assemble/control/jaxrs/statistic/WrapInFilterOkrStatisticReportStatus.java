package com.x.okr.assemble.control.jaxrs.statistic;

import com.x.base.core.project.annotation.FieldDescribe;

public class WrapInFilterOkrStatisticReportStatus {
	@FieldDescribe("查询开始日期")
	private String startDate = null;
	
	@FieldDescribe("中心工作标题")
	private String centerTitle = null;
	
	@FieldDescribe("查询结束日期")
	private String endDate = null;
	
	@FieldDescribe("具体工作ID")
	private String workId = null;
	
	@FieldDescribe("工作类型")
	private String workTypeName = null;
	
	@FieldDescribe("工作责任部门")
	private String unitName = null;
	
	@FieldDescribe("中心工作ID")
	private String centerId = "";
	
	@FieldDescribe("工作汇报周期：每周汇报|每月汇报")
	private String cycleType = "每周汇报";
	
	@FieldDescribe("工作状态：正常|已归档")
	private String status = "正常";
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getWorkId() {
		return workId;
	}
	public void setWorkId(String workId) {
		this.workId = workId;
	}
	
	public String getWorkTypeName() {
		return workTypeName;
	}
	public void setWorkTypeName(String workTypeName) {
		this.workTypeName = workTypeName;
	}
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public String getCenterId() {
		return centerId;
	}
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}
	public String getCycleType() {
		return cycleType;
	}
	public void setCycleType(String cycleType) {
		this.cycleType = cycleType;
	}
	public String getCenterTitle() {
		return centerTitle;
	}
	public void setCenterTitle(String centerTitle) {
		this.centerTitle = centerTitle;
	}
}
