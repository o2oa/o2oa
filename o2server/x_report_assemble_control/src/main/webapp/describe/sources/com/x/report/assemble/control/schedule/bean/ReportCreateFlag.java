package com.x.report.assemble.control.schedule.bean;

import java.util.Date;

import com.x.report.assemble.control.EnumReportTypes;

/**
 * 汇报生成的周期，模块信息
 * 
 * @author O2LEE
 *
 */
public class ReportCreateFlag {
	
	private EnumReportTypes reportType = null;
	
	private String reportYear = null;
	
	private String reportMonth = null;
	
	private String reportWeek = null;
	
	private String reportDate = null;
	
	private String report_modules = null;
	
	private Date sendDate = null;

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

	/**
	 * 获取汇报的类别（枚举）
	 * @return EnumReportTypes
	 */
	public EnumReportTypes getReportType() {
		return reportType;
	}

	public void setReportType(EnumReportTypes reportType) {
		this.reportType = reportType;
	}

	public String getReport_modules() {
		return report_modules;
	}

	public void setReport_modules(String report_modules) {
		this.report_modules = report_modules;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

}