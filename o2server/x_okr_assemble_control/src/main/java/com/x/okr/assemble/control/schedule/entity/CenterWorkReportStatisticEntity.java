package com.x.okr.assemble.control.schedule.entity;

import java.util.List;

public class CenterWorkReportStatisticEntity {
	private String workTypeName = "";
	private String centerId = "";
	private String centerTitle = "";
	private List<BaseWorkReportStatisticEntity> workReportStatisticEntityList = null;
	
	public String getWorkTypeName() {
		return workTypeName;
	}
	public void setWorkTypeName(String workTypeName) {
		this.workTypeName = workTypeName;
	}
	public String getCenterId() {
		return centerId;
	}
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}
	public String getCenterTitle() {
		return centerTitle;
	}
	public void setCenterTitle(String centerTitle) {
		this.centerTitle = centerTitle;
	}
	public List<BaseWorkReportStatisticEntity> getWorkReportStatisticEntityList() {
		return workReportStatisticEntityList;
	}
	public void setWorkReportStatisticEntityList(List<BaseWorkReportStatisticEntity> workReportStatisticEntityList) {
		this.workReportStatisticEntityList = workReportStatisticEntityList;
	}
	
	
}
