package com.x.okr.assemble.control.timertask.entity;

import java.util.List;

public class BaseWorkReportStatisticEntity {
	private String workId = "";
	private String parentWorkId = "";
	private String workLevel = "0";
	private String workTitle = "";
	private String workTypeName = "";
	private String responsibilityIdentity = "";
	private String reportStatus = "未提交汇报";
	private String reportCycle = "不需要汇报";
	/**
	 * 周期汇报时间：每月的几号(1-31)，每周的星期几(1-7)
	 */
	private Integer reportDayInCycle = 0;
	private Boolean needReport = true;
	/**
	 * 责任部门
	 */
	private String organizationName = "";
	/**
	 * 责任部门所属公司
	 */
	private String companyName = "";
	/**
	 * 事项分解及描述
	 */
	private String workDetail = "";
	/**
	 * 具体行动举措
	 */
	private String progressAction = "";
	/**
	 * 预期里程碑/阶段性结果标志
	 */
	private String landmarkDescription = "";
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
	 * 领导评价
	 */
	private List<WorkReportProcessOpinionEntity> opinions = null;
	
	public String getWorkId() {
		return workId;
	}
	public void setWorkId(String workId) {
		this.workId = workId;
	}
	public String getWorkTitle() {
		return workTitle;
	}
	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}
	public String getWorkTypeName() {
		return workTypeName;
	}
	public void setWorkTypeName(String workTypeName) {
		this.workTypeName = workTypeName;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getWorkDetail() {
		return workDetail;
	}
	public void setWorkDetail(String workDetail) {
		this.workDetail = workDetail;
	}
	public String getProgressAction() {
		return progressAction;
	}
	public void setProgressAction(String progressAction) {
		this.progressAction = progressAction;
	}
	public String getLandmarkDescription() {
		return landmarkDescription;
	}
	public void setLandmarkDescription(String landmarkDescription) {
		this.landmarkDescription = landmarkDescription;
	}
	public String getProgressDescription() {
		return progressDescription;
	}
	public void setProgressDescription(String progressDescription) {
		this.progressDescription = progressDescription;
	}
	public String getWorkPlan() {
		return workPlan;
	}
	public void setWorkPlan(String workPlan) {
		this.workPlan = workPlan;
	}
	public String getWorkPointAndRequirements() {
		return workPointAndRequirements;
	}
	public void setWorkPointAndRequirements(String workPointAndRequirements) {
		this.workPointAndRequirements = workPointAndRequirements;
	}
	public String getAdminSuperviseInfo() {
		return adminSuperviseInfo;
	}
	public void setAdminSuperviseInfo(String adminSuperviseInfo) {
		this.adminSuperviseInfo = adminSuperviseInfo;
	}
	public List<WorkReportProcessOpinionEntity> getOpinions() {
		return opinions;
	}
	public void setOpinions(List<WorkReportProcessOpinionEntity> opinions) {
		this.opinions = opinions;
	}
	public String getResponsibilityIdentity() {
		return responsibilityIdentity;
	}
	public void setResponsibilityIdentity(String responsibilityIdentity) {
		this.responsibilityIdentity = responsibilityIdentity;
	}
	public String getParentWorkId() {
		return parentWorkId;
	}
	public void setParentWorkId(String parentWorkId) {
		this.parentWorkId = parentWorkId;
	}
	public Boolean getNeedReport() {
		return needReport;
	}
	public void setNeedReport(Boolean needReport) {
		this.needReport = needReport;
	}
	public String getReportStatus() {
		return reportStatus;
	}
	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}
	public String getReportCycle() {
		return reportCycle;
	}
	public void setReportCycle(String reportCycle) {
		this.reportCycle = reportCycle;
	}
	public String getWorkLevel() {
		return workLevel;
	}
	public void setWorkLevel(String workLevel) {
		this.workLevel = workLevel;
	}
	public Integer getReportDayInCycle() {
		return reportDayInCycle;
	}
	public void setReportDayInCycle(Integer reportDayInCycle) {
		this.reportDayInCycle = reportDayInCycle;
	}
	
}
