package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.WrapOutOkrCenterWorkViewInfo;
import com.x.okr.assemble.control.jaxrs.okrworkauthorizerecord.WrapOutOkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;

@Wrap( OkrWorkBaseInfo.class )
public class WrapOutOkrWorkBaseViewInfo  {
	
	public static List<String> Excludes = new ArrayList<String>();
	
	@EntityFieldDescribe( "工作所属中心工作信息" )
	private WrapOutOkrCenterWorkViewInfo centerWorkInfo = null;
	
	@EntityFieldDescribe( "工作附件信息列表" )
	private List< WrapOutOkrAttachmentFileInfo > workAttachments = null;
	
	@EntityFieldDescribe( "子工作信息列表" )
	private List< WrapOutOkrWorkBaseSimpleInfo > subWorks = null;
	
	@EntityFieldDescribe( "工作汇报信息简单信息列表" )
	private List< WrapOutOkrWorkReportBaseSimpleInfo > workReports = null;
	
	@EntityFieldDescribe( "工作部门以及授权过程列表， 由该工作的上级工作线以及工作授权记录一起组织起来的信息列表" )
	private List< WrapOutOkrWorkDeployAuthorizeRecord > workDeployAuthorizeRecords = null;
	
	@EntityFieldDescribe( "查看者的授权信息, 有可能没有" )
	private WrapOutOkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
	
	@EntityFieldDescribe( "工作ID" )
	private String id = "";
	
	@EntityFieldDescribe( "工作标题" )
	private String title = "";
	
	@EntityFieldDescribe( "中心工作ID" )
	private String centerId = "";
	
	@EntityFieldDescribe( "中心工作标题" )
	private String centerTitle = "";
	
	@EntityFieldDescribe( "上级工作ID" )
	private String parentWorkId = "";
	
	@EntityFieldDescribe( "上级工作标题" )
	private String parentWorkTitle = "";	

	@EntityFieldDescribe( "工作期限类型:短期工作(不需要自动启动定期汇报) | 长期工作（需要自动启动定期汇报）" )
	private String workDateTimeType = "长期工作";
	
	@EntityFieldDescribe( "部署者姓名" )
	private String deployerName = "";
	
	@EntityFieldDescribe( "部署者身份" )
	private String deployerIdentity = "";
	
	@EntityFieldDescribe( "部署者所属组织" )
	private String deployerOrganizationName = "";
	
	@EntityFieldDescribe( "部署者所属公司" )
	private String deployerCompanyName = "";
	
	@EntityFieldDescribe( "工作部署日期-字符串，显示用：yyyy-mm-dd" )
	private String deployDateStr = "";
	
	@EntityFieldDescribe( "工作确认日期-字符串，显示用：yyyy-mm-dd" )
	private String confirmDateStr = "";
	
	@EntityFieldDescribe( "工作完成日期-字符串，显示用：yyyy-mm-dd" )
	private String completeDateLimitStr = "";
	
	@EntityFieldDescribe( "主责人姓名" )
	private String responsibilityEmployeeName = "";
	
	@EntityFieldDescribe( "主责人身份" )
	private String responsibilityIdentity = "";
	
	@EntityFieldDescribe( "主责人所属组织" )
	private String responsibilityOrganizationName = "";
	
	@EntityFieldDescribe( "主责人所属公司" )
	private String responsibilityCompanyName = "";

	@EntityFieldDescribe( "协助人姓名，可能多值" )
	private String cooperateEmployeeName = "";
	
	@EntityFieldDescribe( "协助人身份，可能多值" )
	private String cooperateIdentity = "";
	
	@EntityFieldDescribe( "协助人所属组织，可能多值" )
	private String cooperateOrganizationName = "";
	
	@EntityFieldDescribe( "协助人所属公司，可能多值" )
	private String cooperateCompanyName = "";
	
	@EntityFieldDescribe( "阅知领导身份，可能多值" )
	private String readLeaderIdentity = "";
	
	@EntityFieldDescribe( "阅知领导，可能多值" )
	private String readLeaderName = "";
	
	@EntityFieldDescribe( "阅知领导所属组织，可能多值" )
	private String readLeaderOrganizationName = "";
	
	@EntityFieldDescribe( "阅知领导所属公司，可能多值" )
	private String readLeaderCompanyName = "";
	
	@EntityFieldDescribe( "工作类别" )
	private String workType = "";
	
	@EntityFieldDescribe( "工作级别" )
	private String workLevel = "";
	
	@EntityFieldDescribe( "工作进度" )
	private Double overallProgress = 0.0;
	
	@EntityFieldDescribe( "工作处理状态：草稿|待确认|执行中|已超期|已完成|已撤消" )
	private String workProcessStatus = "草稿";
	
	@EntityFieldDescribe( "工作是否已超期" )
	private Boolean isOverTime = false;
	
	@EntityFieldDescribe( "工作是否已完成" )
	private Boolean isCompleted = false;

	@EntityFieldDescribe( "上一次汇报时间" )
	private Date lastReportTime = null;
	
	@EntityFieldDescribe( "下一次汇报时间" )
	private Date nextReportTime = null;
	
	@EntityFieldDescribe( "已汇报次数" )
	private Integer reportCount = 0;
	
	@EntityFieldDescribe( "汇报周期:不需要汇报|每月汇报|每周汇报" )
	private String reportCycle = "";
	
	@EntityFieldDescribe( "是否需要定期汇报" )
	private Boolean isNeedReport = true;
	
	@EntityFieldDescribe( "周期汇报时间：每月的几号(1-31)，每周的星期几(1-7)，启动时间由系统配置设定，比如：10:00" )
	private Integer reportDayInCycle = 0;
	
	@EntityFieldDescribe( "工作汇报是否需要管理补充信息" )
	private Boolean reportNeedAdminAudit = false;
	
	@EntityFieldDescribe( "工作管理员姓名" )
	@Column( name="xreportAdminName", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String reportAdminName = "";
	
	@EntityFieldDescribe( "工作管理员姓名" )
	@Column( name="xreportAdminIdentity", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String reportAdminIdentity = "";
	
	@EntityFieldDescribe( "工作详细描述, 事项分解" )
	private String workDetail = "";
	
	@EntityFieldDescribe( "职责描述" )
	private String dutyDescription = "";
	
	@EntityFieldDescribe( "里程碑标志说明" )
	private String landmarkDescription = "";
	
	@EntityFieldDescribe( "重点事项说明" )
	private String majorIssuesDescription = "";
	
	@EntityFieldDescribe( "具体行动举措" )
	private String progressAction = "";
	
	@EntityFieldDescribe( "进展计划时限说明" )
	private String progressPlan = "";
	
	@EntityFieldDescribe( "交付成果说明" )
	private String resultDescription = "";
   
	private Long rank = 0L;

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
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

	public String getLandmarkDescription() {
		return landmarkDescription;
	}

	public void setLandmarkDescription(String landmarkDescription) {
		this.landmarkDescription = landmarkDescription;
	}

	public String getMajorIssuesDescription() {
		return majorIssuesDescription;
	}

	public void setMajorIssuesDescription(String majorIssuesDescription) {
		this.majorIssuesDescription = majorIssuesDescription;
	}

	public String getProgressAction() {
		return progressAction;
	}

	public void setProgressAction(String progressAction) {
		this.progressAction = progressAction;
	}

	public String getProgressPlan() {
		return progressPlan;
	}

	public void setProgressPlan(String progressPlan) {
		this.progressPlan = progressPlan;
	}

	public String getResultDescription() {
		return resultDescription;
	}

	public void setResultDescription(String resultDescription) {
		this.resultDescription = resultDescription;
	}

	public WrapOutOkrWorkAuthorizeRecord getOkrWorkAuthorizeRecord() {
		return okrWorkAuthorizeRecord;
	}

	public void setOkrWorkAuthorizeRecord(WrapOutOkrWorkAuthorizeRecord okrWorkAuthorizeRecord) {
		this.okrWorkAuthorizeRecord = okrWorkAuthorizeRecord;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getParentWorkId() {
		return parentWorkId;
	}

	public void setParentWorkId( String parentWorkId ) {
		this.parentWorkId = parentWorkId;
	}

	public String getParentWorkTitle() {
		return parentWorkTitle;
	}

	public void setParentWorkTitle(String parentWorkTitle) {
		this.parentWorkTitle = parentWorkTitle;
	}

	public String getWorkDateTimeType() {
		return workDateTimeType;
	}

	public void setWorkDateTimeType(String workDateTimeType) {
		this.workDateTimeType = workDateTimeType;
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

	public String getDeployDateStr() {
		return deployDateStr;
	}

	public void setDeployDateStr(String deployDateStr) {
		this.deployDateStr = deployDateStr;
	}

	public String getConfirmDateStr() {
		return confirmDateStr;
	}

	public void setConfirmDateStr(String confirmDateStr) {
		this.confirmDateStr = confirmDateStr;
	}

	public String getCompleteDateLimitStr() {
		return completeDateLimitStr;
	}

	public void setCompleteDateLimitStr(String completeDateLimitStr) {
		this.completeDateLimitStr = completeDateLimitStr;
	}

	public String getResponsibilityEmployeeName() {
		return responsibilityEmployeeName;
	}

	public void setResponsibilityEmployeeName(String responsibilityEmployeeName) {
		this.responsibilityEmployeeName = responsibilityEmployeeName;
	}

	public String getResponsibilityIdentity() {
		return responsibilityIdentity;
	}

	public void setResponsibilityIdentity(String responsibilityIdentity) {
		this.responsibilityIdentity = responsibilityIdentity;
	}

	public String getResponsibilityOrganizationName() {
		return responsibilityOrganizationName;
	}

	public void setResponsibilityOrganizationName(String responsibilityOrganizationName) {
		this.responsibilityOrganizationName = responsibilityOrganizationName;
	}

	public String getResponsibilityCompanyName() {
		return responsibilityCompanyName;
	}

	public void setResponsibilityCompanyName(String responsibilityCompanyName) {
		this.responsibilityCompanyName = responsibilityCompanyName;
	}

	public String getCooperateEmployeeName() {
		return cooperateEmployeeName;
	}

	public void setCooperateEmployeeName(String cooperateEmployeeName) {
		this.cooperateEmployeeName = cooperateEmployeeName;
	}

	public String getCooperateIdentity() {
		return cooperateIdentity;
	}

	public void setCooperateIdentity(String cooperateIdentity) {
		this.cooperateIdentity = cooperateIdentity;
	}

	public String getCooperateOrganizationName() {
		return cooperateOrganizationName;
	}

	public void setCooperateOrganizationName(String cooperateOrganizationName) {
		this.cooperateOrganizationName = cooperateOrganizationName;
	}

	public String getCooperateCompanyName() {
		return cooperateCompanyName;
	}

	public void setCooperateCompanyName(String cooperateCompanyName) {
		this.cooperateCompanyName = cooperateCompanyName;
	}

	public String getReadLeaderIdentity() {
		return readLeaderIdentity;
	}

	public void setReadLeaderIdentity(String readLeaderIdentity) {
		this.readLeaderIdentity = readLeaderIdentity;
	}

	public String getReadLeaderName() {
		return readLeaderName;
	}

	public void setReadLeaderName(String readLeaderName) {
		this.readLeaderName = readLeaderName;
	}

	public String getReadLeaderOrganizationName() {
		return readLeaderOrganizationName;
	}

	public void setReadLeaderOrganizationName(String readLeaderOrganizationName) {
		this.readLeaderOrganizationName = readLeaderOrganizationName;
	}

	public String getReadLeaderCompanyName() {
		return readLeaderCompanyName;
	}

	public void setReadLeaderCompanyName(String readLeaderCompanyName) {
		this.readLeaderCompanyName = readLeaderCompanyName;
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

	public Double getOverallProgress() {
		return overallProgress;
	}

	public void setOverallProgress(Double overallProgress) {
		this.overallProgress = overallProgress;
	}

	public String getWorkProcessStatus() {
		return workProcessStatus;
	}

	public void setWorkProcessStatus(String workProcessStatus) {
		this.workProcessStatus = workProcessStatus;
	}

	public Boolean getIsOverTime() {
		return isOverTime;
	}

	public void setIsOverTime(Boolean isOverTime) {
		this.isOverTime = isOverTime;
	}

	public Boolean getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public Date getLastReportTime() {
		return lastReportTime;
	}

	public void setLastReportTime(Date lastReportTime) {
		this.lastReportTime = lastReportTime;
	}

	public Date getNextReportTime() {
		return nextReportTime;
	}

	public void setNextReportTime(Date nextReportTime) {
		this.nextReportTime = nextReportTime;
	}

	public Integer getReportCount() {
		return reportCount;
	}

	public void setReportCount(Integer reportCount) {
		this.reportCount = reportCount;
	}

	public String getReportCycle() {
		return reportCycle;
	}

	public void setReportCycle(String reportCycle) {
		this.reportCycle = reportCycle;
	}

	public Boolean getIsNeedReport() {
		return isNeedReport;
	}

	public void setIsNeedReport(Boolean isNeedReport) {
		this.isNeedReport = isNeedReport;
	}

	public Integer getReportDayInCycle() {
		return reportDayInCycle;
	}

	public void setReportDayInCycle(Integer reportDayInCycle) {
		this.reportDayInCycle = reportDayInCycle;
	}

	public Boolean getReportNeedAdminAudit() {
		return reportNeedAdminAudit;
	}

	public void setReportNeedAdminAudit(Boolean reportNeedAdminAudit) {
		this.reportNeedAdminAudit = reportNeedAdminAudit;
	}

	public String getReportAdminName() {
		return reportAdminName;
	}

	public void setReportAdminName(String reportAdminName) {
		this.reportAdminName = reportAdminName;
	}

	public String getReportAdminIdentity() {
		return reportAdminIdentity;
	}

	public void setReportAdminIdentity(String reportAdminIdentity) {
		this.reportAdminIdentity = reportAdminIdentity;
	}

	public WrapOutOkrCenterWorkViewInfo getCenterWorkInfo() {
		return centerWorkInfo;
	}

	public void setCenterWorkInfo(WrapOutOkrCenterWorkViewInfo centerWorkInfo) {
		this.centerWorkInfo = centerWorkInfo;
	}

	public List<WrapOutOkrAttachmentFileInfo> getWorkAttachments() {
		return workAttachments;
	}

	public void setWorkAttachments(List<WrapOutOkrAttachmentFileInfo> workAttachments) {
		this.workAttachments = workAttachments;
	}

	public List<WrapOutOkrWorkReportBaseSimpleInfo> getWorkReports() {
		return workReports;
	}

	public void setWorkReports(List<WrapOutOkrWorkReportBaseSimpleInfo> workReports) {
		this.workReports = workReports;
	}

	public List<WrapOutOkrWorkDeployAuthorizeRecord> getWorkDeployAuthorizeRecords() {
		return workDeployAuthorizeRecords;
	}

	public void setWorkDeployAuthorizeRecords(List<WrapOutOkrWorkDeployAuthorizeRecord> workDeployAuthorizeRecords) {
		this.workDeployAuthorizeRecords = workDeployAuthorizeRecords;
	}

	public List<WrapOutOkrWorkBaseSimpleInfo> getSubWorks() {
		return subWorks;
	}

	public void setSubWorks(List<WrapOutOkrWorkBaseSimpleInfo> subWorks) {
		this.subWorks = subWorks;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}