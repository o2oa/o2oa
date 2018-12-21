package com.x.okr.assemble.control.jaxrs.statistic;

import com.x.base.core.project.annotation.FieldDescribe;

public class WrapInFilterOkrStatisticReportContent {
	@FieldDescribe("工作汇报周期：每周汇报|每月汇报")
	private String reportCycle = null;
	
	@FieldDescribe("中心工作ID")
	private String centerId = null;
	
	@FieldDescribe("中心工作标题")
	private String centerTitle = null;
	
	@FieldDescribe("工作类型名称")
	private String workTypeName = null;
	
	@FieldDescribe("上级工作ID")
	private String parentWorkId = null;
	
	@FieldDescribe("工作级别")
	private String workLevel = null;
	
	@FieldDescribe("统计时间标识")
	private String statisticTimeFlag = null;
	
	@FieldDescribe("开始日期")
	private String startDate = null;
	
	@FieldDescribe("结束日期")
	private String endDate = null;
	
	@FieldDescribe("统计年份")
	private Integer year = 0;
	
	@FieldDescribe("统计月份")
	private Integer month = 0;
	
	@FieldDescribe("统计周数")
	private Integer week = 0;
	
	@FieldDescribe("工作状态：正常|已归档")
	private String status = "正常";
	
	@FieldDescribe("排序类型")
	private String order = "DESC";
	
	private Boolean stream = true;	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReportCycle() {
		return reportCycle;
	}
	public void setReportCycle(String reportCycle) {
		this.reportCycle = reportCycle;
	}
	public String getCenterId() {
		return centerId;
	}
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}
	public String getWorkTypeName() {
		return workTypeName;
	}
	public void setWorkTypeName(String workTypeName) {
		this.workTypeName = workTypeName;
	}
	public String getParentWorkId() {
		return parentWorkId;
	}
	public void setParentWorkId(String parentWorkId) {
		this.parentWorkId = parentWorkId;
	}
	public String getWorkLevel() {
		return workLevel;
	}
	public void setWorkLevel(String workLevel) {
		this.workLevel = workLevel;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	public Integer getWeek() {
		return week;
	}
	public void setWeek(Integer week) {
		this.week = week;
	}

	public String getStatisticTimeFlag() {
		return statisticTimeFlag;
	}
	public void setStatisticTimeFlag(String statisticTimeFlag) {
		this.statisticTimeFlag = statisticTimeFlag;
	}
	public String getStartDate() {
		return startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public Boolean getStream() {
		return stream;
	}
	public void setStream(Boolean stream) {
		this.stream = stream;
	}
	public String getCenterTitle() {
		return centerTitle;
	}
	public void setCenterTitle(String centerTitle) {
		this.centerTitle = centerTitle;
	}
}
