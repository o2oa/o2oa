package com.x.okr.assemble.control.schedule.entity;

import java.util.List;

public class BaseWorkReportStatisticEntity {

	private String reportId = null;
	
	private String reportStatus = "未提交汇报";
	
	/**
	 * 截止目前完成情况
	 */
	private String progressDescription = "";
	/**
	 * 后续工作计划
	 */
	private String workPlan = "";
	/**
	 * 下一步工作要点及需求
	 * 工作要点及需求
	 */
	private String workPointAndRequirements  = "";
	/**
	 * 督办评价
	 */
	private String adminSuperviseInfo = "";
	
	/**
	 * 工作汇报备注信息
	 */
	private String reportMemo = "";
	
	/**
	 * 领导评价
	 */
	private List<WorkReportProcessOpinionEntity> opinions = null;
	
	public String getReportStatus() {
		return reportStatus;
	}
	public String getProgressDescription() {
		return progressDescription;
	}
	public String getWorkPlan() {
		return workPlan;
	}
	public String getWorkPointAndRequirements() {
		return workPointAndRequirements;
	}
	public String getAdminSuperviseInfo() {
		return adminSuperviseInfo;
	}
	public List<WorkReportProcessOpinionEntity> getOpinions() {
		return opinions;
	}
	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}
	public void setProgressDescription(String progressDescription) {
		this.progressDescription = progressDescription;
	}
	public void setWorkPlan(String workPlan) {
		this.workPlan = workPlan;
	}
	public void setWorkPointAndRequirements(String workPointAndRequirements) {
		this.workPointAndRequirements = workPointAndRequirements;
	}
	public void setAdminSuperviseInfo(String adminSuperviseInfo) {
		this.adminSuperviseInfo = adminSuperviseInfo;
	}
	public void setOpinions( List<WorkReportProcessOpinionEntity> opinions ) {
		this.opinions = opinions;
	}
	public String getReportMemo() {
		return reportMemo;
	}
	public void setReportMemo(String reportMemo) {
		this.reportMemo = reportMemo;
	}
	public String getReportId() {
		return reportId;
	}
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	
}
