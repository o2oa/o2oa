package com.x.okr.assemble.control.jaxrs.statistic;
import com.x.base.core.http.annotation.Wrap;

@Wrap( WrapInFilterOkrStatisticReportStatus.class)
public class WrapInFilterOkrStatisticReportStatus {
	
	private String startDate = null;
	private String centerTitle = null;
	private String endDate = null;
	private String workId = null;
	private String workTypeName = null;
	private String organization = null;
	private String centerId = "";
	private String cycleType = "每周汇报";
	
	
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
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
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
