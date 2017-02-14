package com.x.okr.assemble.control.jaxrs.statistic;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrCenterWorkReportStatistic;

@Wrap( OkrCenterWorkReportStatistic.class)
public class OkrCenterWorkReportStatisticWrapInFilter extends GsonPropertyObject {
	
	private String reportCycle = null;
	private String centerId = null;
	private String workTypeName = null;
	private String parentWorkId = null;
	private String workLevel = null;
	private Integer year = 0;
	private Integer month = 0;
	private Integer week = 0;
	
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
	
}
