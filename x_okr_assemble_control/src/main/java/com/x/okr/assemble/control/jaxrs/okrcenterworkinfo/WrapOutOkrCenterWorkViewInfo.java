package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;

@Wrap( WrapOutOkrCenterWorkViewInfo.class )
public class WrapOutOkrCenterWorkViewInfo  {

	public static List<String> Excludes = new ArrayList<String>();
	
	@EntityFieldDescribe( "中心工作ID" )
	private String id = "";
	
	@EntityFieldDescribe( "中心标题" )
	private String title = "";
	
	@EntityFieldDescribe( "部署者姓名" )
	private String deployerName = "";
	
	@EntityFieldDescribe( "部署者身份" )
	private String deployerIdentity = "";
	
	@EntityFieldDescribe( "部署者所属组织" )
	private String deployerOrganizationName = "";
	
	@EntityFieldDescribe( "部署者所属公司" )
	private String deployerCompanyName = "";
	
	@EntityFieldDescribe( "审核者姓名" )
	private String auditLeaderName = "";
	
	@EntityFieldDescribe( "审核者身份" )
	private String auditLeaderIdentity = "";
	
	@EntityFieldDescribe( "中心工作处理状态：草稿|待审核|待确认|执行中|已完成|已撤消" )
	private String processStatus = "草稿";
	
	@EntityFieldDescribe( "中心工作默认完成日期-字符串，显示用：yyyy-mm-dd" )
	private String defaultCompleteDateLimitStr = "";
	
	@EntityFieldDescribe( "中心工作默认工作类别" )
	private String defaultWorkType = "";
	
	@EntityFieldDescribe( "中心工作默认工作级别" )
	private String defaultWorkLevel = "";
	
	@EntityFieldDescribe( "中心工作默认阅知领导(可多值，显示用)" )
	private String defaultLeader = "";
	
	@EntityFieldDescribe( "中心工作默认阅知领导身份(可多值，计算组织和公司用)" )
	private String defaultLeaderIdentity = "";
	
	@EntityFieldDescribe( "工作汇报审批领导(可多值，显示用)" )
	private String reportAuditLeaderName = "";
	
	@EntityFieldDescribe( "工作汇报审批领导身份(可多值，计算组织和公司用)" )
	private String reportAuditLeaderIdentity = "";
	
	@EntityFieldDescribe( "中心工作是否需要审核" )
	private Boolean isNeedAudit = false;

	@EntityFieldDescribe( "处理状态：正常|已删除" )
	private String status = "正常";
	
	@EntityFieldDescribe( "中心工作描述" )
	private String description = "";
	
	@EntityFieldDescribe( "工作处理职责身份(多值): VIEW(观察者)|DEPLOY(部署者)|RESPONSIBILITY(责任者)|COOPERATE(协助者)|READ(阅知者)|REPORTAUDIT(汇报审核者)" )
	private List<String> workProcessIdentity = null;
	
	@EntityFieldDescribe( "用户可以对工作进行的操作(多值):VIEW|EDIT|DELETE" )
	private List<String> operation = null;
	
	private Boolean watch = false;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDeployerName() {
		return deployerName;
	}

	public void setDeployerName(String deployerName) {
		this.deployerName = deployerName;
	}

	public String getDeployerIdentity() {
		return deployerIdentity;
	}

	public void setDeployerIdentity(String deployerIdentity) {
		this.deployerIdentity = deployerIdentity;
	}

	public String getDeployerOrganizationName() {
		return deployerOrganizationName;
	}

	public void setDeployerOrganizationName(String deployerOrganizationName) {
		this.deployerOrganizationName = deployerOrganizationName;
	}

	public String getDeployerCompanyName() {
		return deployerCompanyName;
	}

	public void setDeployerCompanyName(String deployerCompanyName) {
		this.deployerCompanyName = deployerCompanyName;
	}

	public String getAuditLeaderName() {
		return auditLeaderName;
	}

	public void setAuditLeaderName(String auditLeaderName) {
		this.auditLeaderName = auditLeaderName;
	}

	public String getAuditLeaderIdentity() {
		return auditLeaderIdentity;
	}

	public void setAuditLeaderIdentity(String auditLeaderIdentity) {
		this.auditLeaderIdentity = auditLeaderIdentity;
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	public String getDefaultCompleteDateLimitStr() {
		return defaultCompleteDateLimitStr;
	}

	public void setDefaultCompleteDateLimitStr(String defaultCompleteDateLimitStr) {
		this.defaultCompleteDateLimitStr = defaultCompleteDateLimitStr;
	}

	public String getDefaultWorkType() {
		return defaultWorkType;
	}

	public void setDefaultWorkType(String defaultWorkType) {
		this.defaultWorkType = defaultWorkType;
	}

	public String getDefaultWorkLevel() {
		return defaultWorkLevel;
	}

	public void setDefaultWorkLevel(String defaultWorkLevel) {
		this.defaultWorkLevel = defaultWorkLevel;
	}

	public String getDefaultLeader() {
		return defaultLeader;
	}

	public void setDefaultLeader(String defaultLeader) {
		this.defaultLeader = defaultLeader;
	}

	public String getDefaultLeaderIdentity() {
		return defaultLeaderIdentity;
	}

	public void setDefaultLeaderIdentity(String defaultLeaderIdentity) {
		this.defaultLeaderIdentity = defaultLeaderIdentity;
	}

	public String getReportAuditLeaderName() {
		return reportAuditLeaderName;
	}

	public void setReportAuditLeaderName(String reportAuditLeaderName) {
		this.reportAuditLeaderName = reportAuditLeaderName;
	}

	public String getReportAuditLeaderIdentity() {
		return reportAuditLeaderIdentity;
	}

	public void setReportAuditLeaderIdentity(String reportAuditLeaderIdentity) {
		this.reportAuditLeaderIdentity = reportAuditLeaderIdentity;
	}

	public Boolean getIsNeedAudit() {
		return isNeedAudit;
	}

	public void setIsNeedAudit(Boolean isNeedAudit) {
		this.isNeedAudit = isNeedAudit;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getWatch() {
		return watch;
	}

	public void setWatch(Boolean watch) {
		this.watch = watch;
	}

	public List<String> getWorkProcessIdentity() {
		return workProcessIdentity;
	}

	public void setWorkProcessIdentity(List<String> workProcessIdentity) {
		this.workProcessIdentity = workProcessIdentity;
	}

	public List<String> getOperation() {
		return operation;
	}

	public void setOperation(List<String> operation) {
		this.operation = operation;
	}
	
}