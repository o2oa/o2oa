package com.x.attendance.assemble.control.jaxrs.fileimport;

import java.io.Serializable;

import com.x.base.core.gson.GsonPropertyObject;

public class CacheImportRowDetail extends GsonPropertyObject implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String employeeNo = "";
	
	private String employeeName = "";
	
	private String recordDateString = "";	
	
	private String recordYearString = "";
	
	private String recordMonthString = "";
	
	private String onDutyTime = "";
	
	private String offDutyTime = "";
	
	private String onDutyTimeFormated = "";
	
	private String offDutyTimeFormated = "";
	
	private String recordDateStringFormated = "";
	
	private String description = "";
	
	private String checkStatus = "success";

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getRecordDateString() {
		return recordDateString;
	}

	public void setRecordDateString(String recordDateString) {
		this.recordDateString = recordDateString;
	}

	public String getOnDutyTime() {
		return onDutyTime;
	}

	public void setOnDutyTime(String onDutyTime) {
		this.onDutyTime = onDutyTime;
	}

	public String getOffDutyTime() {
		return offDutyTime;
	}

	public void setOffDutyTime(String offDutyTime) {
		this.offDutyTime = offDutyTime;
	}

	public String getOnDutyTimeFormated() {
		return onDutyTimeFormated;
	}

	public void setOnDutyTimeFormated(String onDutyTimeFormated) {
		this.onDutyTimeFormated = onDutyTimeFormated;
	}

	public String getOffDutyTimeFormated() {
		return offDutyTimeFormated;
	}

	public void setOffDutyTimeFormated(String offDutyTimeFormated) {
		this.offDutyTimeFormated = offDutyTimeFormated;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}

	public String getRecordDateStringFormated() {
		return recordDateStringFormated;
	}

	public void setRecordDateStringFormated(String recordDateStringFormated) {
		this.recordDateStringFormated = recordDateStringFormated;
	}

	public String getRecordYearString() {
		return recordYearString;
	}

	public void setRecordYearString(String recordYearString) {
		this.recordYearString = recordYearString;
	}

	public String getRecordMonthString() {
		return recordMonthString;
	}

	public void setRecordMonthString(String recordMonthString) {
		this.recordMonthString = recordMonthString;
	}
	
}
