package com.x.okr.assemble.control.jaxrs.workimport;

import java.io.Serializable;
import java.util.Date;

import com.x.base.core.project.gson.GsonPropertyObject;

public class CacheImportRowDetail extends GsonPropertyObject implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 工作标题
	 */
	private String title = "";
	
	/**
	 * 上级工作ID
	 */
	private String parentWorkId = "";

	/**
	 * 工作期限类型:短期工作(不需要自动启动定期汇报) | 长期工作（需要自动启动定期汇报）
	 */
	private String workDateTimeType = "长期工作";
	
	/**
	 * 部署者身份
	 */
	private String deployerIdentity = "";

	/**
	 * 创建者身份
	 */
	private String creatorIdentity = "";

	/**
	 * 工作完成日期-字符串，显示用：yyyy-mm-dd
	 */
	private String completeDateLimitStr = "";
	
	/**
	 * 工作完成日期-字符串，显示用：yyyy-mm-dd
	 */
	private Date completeDateLimit = null;

	/**
	 * 主责人身份
	 */
	private String responsibilityIdentity = "";

	/**
	 * 协助人身份，可能多值，用逗号分隔
	 */
	private String cooperateIdentity = "";
	
	/**
	 * 阅知领导身份，可能多值，用逗号分隔
	 */
	private String readLeaderIdentity = "";
	
	/**
	 * 工作类别
	 */
	private String workType = "";
	
	/**
	 * 工作级别
	 */
	private String workLevel = "";
	
	/**
	 * 备注说明
	 */
	private String description = "";

	/**
	 * 工作详细描述
	 */
	private String workDetail = ""; 
	
	/**
	 * 职责描述
	 */
	private String dutyDescription = "";
	
	/**
	 * 具体行动举措
	 */
	private String progressAction = "";
	
	/**
	 * 里程碑标志说明
	 */
	private String landmarkDescription = "";
	
	/**
	 * 交付成果说明
	 */
	private String resultDescription = "";
	
	/**
	 * 重点事项说明
	 */
	private String majorIssuesDescription = "";
	
	/**
	 * 进展计划时限说明
	 */
	private String progressPlan = "";
	
	/**
	 * 汇报周期:不需要汇报|每月汇报|每周汇报
	 */
	private String reportCycle = null;
	
	/**
	 * 周期汇报时间：每月的几号(1-31)，每周的星期几(1-7)，启动时间由系统配置设定，比如：10:00
	 */
	private Integer reportDayInCycle = 0;
	
	/**
	 * 检查状态
	 */
	private String checkStatus = "success";
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getParentWorkId() {
		return parentWorkId;
	}

	public void setParentWorkId(String parentWorkId) {
		this.parentWorkId = parentWorkId;
	}

	public String getWorkDateTimeType() {
		return workDateTimeType;
	}

	public void setWorkDateTimeType(String workDateTimeType) {
		this.workDateTimeType = workDateTimeType;
	}

	public String getDeployerIdentity() {
		return deployerIdentity;
	}

	public void setDeployerIdentity(String deployerIdentity) {
		this.deployerIdentity = deployerIdentity;
	}

	public String getCreatorIdentity() {
		return creatorIdentity;
	}

	public void setCreatorIdentity(String creatorIdentity) {
		this.creatorIdentity = creatorIdentity;
	}

	public String getCompleteDateLimitStr() {
		return completeDateLimitStr;
	}

	public void setCompleteDateLimitStr(String completeDateLimitStr) {
		this.completeDateLimitStr = completeDateLimitStr;
	}

	public String getResponsibilityIdentity() {
		return responsibilityIdentity;
	}

	public void setResponsibilityIdentity(String responsibilityIdentity) {
		this.responsibilityIdentity = responsibilityIdentity;
	}

	public String getCooperateIdentity() {
		return cooperateIdentity;
	}

	public void setCooperateIdentity(String cooperateIdentity) {
		this.cooperateIdentity = cooperateIdentity;
	}

	public String getReadLeaderIdentity() {
		return readLeaderIdentity;
	}

	public void setReadLeaderIdentity(String readLeaderIdentity) {
		this.readLeaderIdentity = readLeaderIdentity;
	}

	public String getWorkType() {
		return workType;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}

	public String getWorkLevel() {
		return workLevel;
	}

	public void setWorkLevel(String workLevel) {
		this.workLevel = workLevel;
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

	public String getWorkDetail() {
		return workDetail;
	}

	public void setWorkDetail(String workDetail) {
		this.workDetail = workDetail;
	}

	public String getDutyDescription() {
		return dutyDescription;
	}

	public void setDutyDescription(String dutyDescription) {
		this.dutyDescription = dutyDescription;
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

	public String getResultDescription() {
		return resultDescription;
	}

	public void setResultDescription(String resultDescription) {
		this.resultDescription = resultDescription;
	}

	public String getMajorIssuesDescription() {
		return majorIssuesDescription;
	}

	public void setMajorIssuesDescription(String majorIssuesDescription) {
		this.majorIssuesDescription = majorIssuesDescription;
	}

	public String getProgressPlan() {
		return progressPlan;
	}

	public void setProgressPlan(String progressPlan) {
		this.progressPlan = progressPlan;
	}

	public String getReportCycle() {
		return reportCycle;
	}

	public void setReportCycle(String reportCycle) {
		this.reportCycle = reportCycle;
	}

	public Integer getReportDayInCycle() {
		return reportDayInCycle;
	}

	public void setReportDayInCycle(Integer reportDayInCycle) {
		this.reportDayInCycle = reportDayInCycle;
	}

	public Date getCompleteDateLimit() {
		return completeDateLimit;
	}

	public void setCompleteDateLimit(Date completeDateLimit) {
		this.completeDateLimit = completeDateLimit;
	}
	
}
