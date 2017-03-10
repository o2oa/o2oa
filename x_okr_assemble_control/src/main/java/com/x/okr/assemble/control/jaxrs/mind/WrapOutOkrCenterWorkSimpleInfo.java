package com.x.okr.assemble.control.jaxrs.mind;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrCenterWorkInfo;

@Wrap( OkrCenterWorkInfo.class )
public class WrapOutOkrCenterWorkSimpleInfo  {

	public static List<String> Excludes = new ArrayList<String>();
	
	@EntityFieldDescribe( "中心工作ID" )
	private String id = null;
	
	@EntityFieldDescribe( "中心标题" )
	private String title = null;
	
	@EntityFieldDescribe( "部署者姓名" )
	private String deployerName = null;
	
	@EntityFieldDescribe( "部署者身份" )
	private String deployerIdentity = null;
	
	@EntityFieldDescribe( "部署者所属组织" )
	private String deployerOrganizationName = null;
	
	@EntityFieldDescribe( "部署者所属公司" )
	private String deployerCompanyName = null;
	
	@EntityFieldDescribe( "审核者姓名" )
	private String auditLeaderName = null;
	
	@EntityFieldDescribe( "审核者身份" )
	private String auditLeaderIdentity = null;
	
	@EntityFieldDescribe( "中心工作处理状态：草稿|待审核|待确认|执行中|已完成|已撤消" )
	private String processStatus = "草稿";
	
	@EntityFieldDescribe( "中心工作默认完成日期-字符串，显示用：yyyy-mm-dd" )
	private String defaultCompleteDateLimitStr = null;
	
	@EntityFieldDescribe( "中心工作默认工作类别" )
	private String defaultWorkType = null;
	
	@EntityFieldDescribe( "中心工作默认工作级别" )
	private String defaultWorkLevel = null;
	
	@EntityFieldDescribe( "中心工作默认阅知领导(可多值，显示用)" )
	private String defaultLeader = null;
	
	@EntityFieldDescribe( "中心工作默认阅知领导身份(可多值，计算组织和公司用)" )
	private String defaultLeaderIdentity = null;
	
	@EntityFieldDescribe( "工作汇报审批领导(可多值，显示用)" )
	private String reportAuditLeaderName = null;
	
	@EntityFieldDescribe( "工作汇报审批领导身份(可多值，计算组织和公司用)" )
	private String reportAuditLeaderIdentity = null;
	
	@EntityFieldDescribe( "中心工作是否需要审核" )
	private Boolean isNeedAudit = false;

	@EntityFieldDescribe( "处理状态：正常|已删除" )
	private String status = "正常";
	
	@EntityFieldDescribe( "中心工作描述" )
	private String description = null;
	
	@EntityFieldDescribe( "中心工作创建时间" )
	private Date createTime = null;
	
	@EntityFieldDescribe( "中心工作包括的工作列表" )
	private List<WrapOutOkrWorkBaseSimpleInfo> works = null;
	
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

	public List<WrapOutOkrWorkBaseSimpleInfo> getWorks() {
		return works;
	}

	public void setWorks(List<WrapOutOkrWorkBaseSimpleInfo> works) {
		this.works = works;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Boolean getWatch() {
		return watch;
	}

	public void setWatch(Boolean watch) {
		this.watch = watch;
	}
	
}