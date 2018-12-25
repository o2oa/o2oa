package com.x.okr.assemble.control.schedule.entity;

import java.util.Date;

import com.x.base.core.project.annotation.FieldDescribe;

public class WorkBaseReportSubmitEntity {
	
	@FieldDescribe( "本周期开始日期:yyyy-mm-dd" )
	private String startDate = "1999-01-01";
	
	@FieldDescribe( "本周期结束日期:yyyy-mm-dd" )
	private String endDate = "1999-01-01";
	
	@FieldDescribe( "工作汇报周期: WEEK|MONTH|QUARTER" )
	private String cycleType = "WEEK";
	
	@FieldDescribe( "本期(开始结束时间)在自然年度中的位置:比如第3周，第2个月" )
	private Integer cycleNumber = 1;
	
	@FieldDescribe( "本期已经提交的汇报ID" )
	private String reportId = null;
	
	@FieldDescribe( "工作汇报提交时间." )
	private Date submitTime = null;
	
	@FieldDescribe( "工作汇报内容是否已经完成: 0-未提交(或者未填写内容)|1-已提交(或者已经填写内容未提交)|-1-无须汇报." )
	private Integer reportStatus = -1;
	
	@FieldDescribe( "说明备注,一般记录错误." )
	private String description = null;

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

	public String getCycleType() {
		return cycleType;
	}

	public void setCycleType(String cycleType) {
		this.cycleType = cycleType;
	}

	public Integer getCycleNumber() {
		return cycleNumber;
	}

	public void setCycleNumber(Integer cycleNumber) {
		this.cycleNumber = cycleNumber;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public Date getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}

	public Integer getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(Integer reportStatus) {
		this.reportStatus = reportStatus;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
