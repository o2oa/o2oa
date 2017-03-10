package com.x.okr.assemble.control.jaxrs.statistic;
import com.x.base.core.http.annotation.Wrap;

@Wrap( WrapInFilterOkrStatisticReportContent.class)
public class WrapInFilterOkrStatisticReportContent {
	
	private String reportCycle = null;
	private String centerId = null;
	private String workTypeName = null;
	private String parentWorkId = null;
	private String workLevel = null;
	private String statisticTimeFlag = null;
	private String startDate = null;
	private String endDate = null;
	private Integer year = 0;
	private Integer month = 0;
	private Integer week = 0;
	private String order = "DESC";
	private Boolean stream = true;
	
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
}
