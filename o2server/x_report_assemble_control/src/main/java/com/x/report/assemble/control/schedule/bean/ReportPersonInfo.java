package com.x.report.assemble.control.schedule.bean;

import java.util.List;

import com.x.report.assemble.control.EnumReportModules;
import com.x.report.assemble.control.EnumReportTypes;

public class ReportPersonInfo {

private String name = null;
	
	private List<EnumReportModules> reportModules = null;
	
	private EnumReportTypes reportType = null;
	
	private String reportYear = null;
	
	private String reportMonth = null;
	
	private String reportWeek = null;
	
	private String reportDate = null;

	public List<EnumReportModules> getReportModules() {
		return reportModules;
	}

	public EnumReportTypes getReportType() {
		return reportType;
	}

	public String getReportYear() {
		return reportYear;
	}

	public String getReportMonth() {
		return reportMonth;
	}

	public String getReportWeek() {
		return reportWeek;
	}

	public String getReportDate() {
		return reportDate;
	}	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setReportModules(List<EnumReportModules> reportModules) {
		this.reportModules = reportModules;
	}

	public void setReportType(EnumReportTypes reportType) {
		this.reportType = reportType;
	}

	public void setReportYear(String reportYear) {
		this.reportYear = reportYear;
	}

	public void setReportMonth(String reportMonth) {
		this.reportMonth = reportMonth;
	}

	public void setReportWeek(String reportWeek) {
		this.reportWeek = reportWeek;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

}