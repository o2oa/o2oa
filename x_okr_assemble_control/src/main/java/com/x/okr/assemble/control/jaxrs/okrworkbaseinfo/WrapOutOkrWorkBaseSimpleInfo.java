package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;

@Wrap( WrapOutOkrWorkBaseSimpleInfo.class )
public class WrapOutOkrWorkBaseSimpleInfo  {
	
	public static List<String> Excludes = new ArrayList<String>();
	
	@EntityFieldDescribe( "子工作信息列表" )
	private List< WrapOutOkrWorkBaseSimpleInfo > subWorks = null;
	
	@EntityFieldDescribe( "工作ID" )
	private String id = null;
	
	@EntityFieldDescribe( "上级工作ID" )
	private String parentWorkId = null;
	
	@EntityFieldDescribe( "工作标题" )
	private String title = null;
	
	@EntityFieldDescribe( "中心工作ID" )
	private String centerId = null;
	
	@EntityFieldDescribe( "中心工作标题" )
	private String centerTitle = null;
	
	@EntityFieldDescribe( "部署者姓名" )
	private String deployerName = null;
	
	@EntityFieldDescribe( "部署者身份" )
	private String deployerIdentity = null;
	
	@EntityFieldDescribe( "部署者所属组织" )
	private String deployerOrganizationName = null;
	
	@EntityFieldDescribe( "部署者所属公司" )
	private String deployerCompanyName = null;
	
	@EntityFieldDescribe( "主责人姓名" )
	private String responsibilityEmployeeName = null;
	
	@EntityFieldDescribe( "主责人身份" )
	private String responsibilityIdentity = null;
	
	@EntityFieldDescribe( "主责人所属组织" )
	private String responsibilityOrganizationName = null;
	
	@EntityFieldDescribe( "主责人所属公司" )
	private String responsibilityCompanyName = null;
	
	@EntityFieldDescribe( "协助人姓名，可能多值" )
	private String cooperateEmployeeName = null;
	
	@EntityFieldDescribe( "协助人身份，可能多值" )
	private String cooperateIdentity = null;
	
	@EntityFieldDescribe( "协助人所属组织，可能多值" )
	private String cooperateOrganizationName = null;
	
	@EntityFieldDescribe( "协助人所属公司，可能多值" )
	private String cooperateCompanyName = null;
	
	@EntityFieldDescribe( "阅知领导身份，可能多值" )
	private String readLeaderIdentity = null;
	
	@EntityFieldDescribe( "阅知领导，可能多值" )
	private String readLeaderName = null;
	
	@EntityFieldDescribe( "阅知领导所属组织，可能多值" )
	private String readLeaderOrganizationName = null;
	
	@EntityFieldDescribe( "阅知领导所属公司，可能多值" )
	private String readLeaderCompanyName = null;
	
	@EntityFieldDescribe( "工作类别" )
	private String workType = null;
	
	@EntityFieldDescribe( "工作级别" )
	private String workLevel = null;
	
	@EntityFieldDescribe( "工作进度" )
	private Double overallProgress = 0.0;
	
	@EntityFieldDescribe( "工作处理状态：草稿|待确认|执行中|已超期|已完成|已撤消" )
	private String workProcessStatus = "草稿";
	
	@EntityFieldDescribe( "工作是否已超期" )
	private Boolean isOverTime = false;
	
	@EntityFieldDescribe( "工作是否已完成" )
	private Boolean isCompleted = false;
	
	@EntityFieldDescribe( "工作详细描述, 事项分解" )
	private String workDetail = null;
	
	@EntityFieldDescribe( "职责描述" )
	private String dutyDescription = null;
	
	@EntityFieldDescribe( "里程碑标志说明" )
	private String landmarkDescription = null;
	
	@EntityFieldDescribe( "重点事项说明" )
	private String majorIssuesDescription = null;
	
	@EntityFieldDescribe( "具体行动举措" )
	private String progressAction = null;
	
	@EntityFieldDescribe( "进展计划时限说明" )
	private String progressPlan = null;
	
	@EntityFieldDescribe( "交付成果说明" )
	private String resultDescription = null;
	
	@EntityFieldDescribe( "是否可以查看工作详情" )
	private Boolean watch = false;
	
	@EntityFieldDescribe( "完成时限" )
	private String completeDateLimitStr = null;
	
	@EntityFieldDescribe( "工作处理职责身份(多值): AUTHORZE(授权中)|TACKBACK(授权收回)|AUTHORIZECANCEL(授权失效)|VIEW(观察者)|RESPONSIBILITY(责任者)|COOPERATE(协助者)|READ(阅知者)" )
	private List<String> workProcessIdentity = null;
	
	@EntityFieldDescribe( "用户可以对工作进行的操作(多值):VIEW|EDIT|SPLIT|AUTHORIZE|TACKBACK|REPORT|DELETE|" )
	private List<String> operation = null;
   
	@EntityFieldDescribe( "工作信息状态：正常|已删除|已归档" )
	private String status = "正常";
	
	@EntityFieldDescribe( "标识工作信息是具体工作,还是中心工作：WORK|CENTER" )
	private String workOrCenter = "WORK";
	
	@EntityFieldDescribe( "标识工作信息是否已经被拆解过了,是否存在下级工作信息" )
	private Boolean hasSubWorks = false;
	
	private Date createTime;
	
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

	public Boolean getWatch() {
		return watch;
	}

	public void setWatch(Boolean watch) {
		this.watch = watch;
	}

	public String getParentWorkId() {
		return parentWorkId;
	}

	public void setParentWorkId(String parentWorkId) {
		this.parentWorkId = parentWorkId;
	}

	public void addNewSubWorkBaseInfo( WrapOutOkrWorkBaseSimpleInfo work ) {
		if( subWorks == null ){
			subWorks = new ArrayList<WrapOutOkrWorkBaseSimpleInfo>();
		}
		subWorks.add( work );
	}

	public String getCompleteDateLimitStr() {
		return completeDateLimitStr;
	}

	public void setCompleteDateLimitStr(String completeDateLimitStr) {
		this.completeDateLimitStr = completeDateLimitStr;
	}

	public String getCooperateOrganizationName() {
		return cooperateOrganizationName;
	}

	public void setCooperateOrganizationName(String cooperateOrganizationName) {
		this.cooperateOrganizationName = cooperateOrganizationName;
	}

	public String getCooperateIdentity() {
		return cooperateIdentity;
	}

	public void setCooperateIdentity(String cooperateIdentity) {
		this.cooperateIdentity = cooperateIdentity;
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

	public String getCooperateEmployeeName() {
		return cooperateEmployeeName;
	}

	public void setCooperateEmployeeName(String cooperateEmployeeName) {
		this.cooperateEmployeeName = cooperateEmployeeName;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getWorkOrCenter() {
		return workOrCenter;
	}

	public void setWorkOrCenter(String workOrCenter) {
		this.workOrCenter = workOrCenter;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Boolean getHasSubWorks() {
		return hasSubWorks;
	}

	public void setHasSubWorks(Boolean hasSubWorks) {
		this.hasSubWorks = hasSubWorks;
	}
	
}