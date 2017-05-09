package com.x.okr.assemble.control.jaxrs.mind;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrWorkBaseInfo;

@Wrap( OkrWorkBaseInfo.class )
public class WrapOutOkrWorkBaseSimpleInfo  {
	
	public static List<String> Excludes = new ArrayList<String>();
	
	@EntityFieldDescribe( "子工作信息列表" )
	private List< WrapOutOkrWorkBaseSimpleInfo > subWorks = null;
	
	@EntityFieldDescribe( "工作ID" )
	private String id = null;
	
	@EntityFieldDescribe( "上级工作ID" )
	private String parentId = null;
	
	@EntityFieldDescribe( "工作标题" )
	private String title = null;
	
	@EntityFieldDescribe( "中心工作ID" )
	private String centerId = null;
	
	@EntityFieldDescribe( "中心工作标题" )
	private String centerTitle = null;
	
	@EntityFieldDescribe( "主责人姓名" )
	private String responsibilityEmployeeName = null;
	
	@EntityFieldDescribe( "主责人身份" )
	private String responsibilityIdentity = null;
	
	@EntityFieldDescribe( "主责人所属组织" )
	private String responsibilityOrganizationName = null;
	
	@EntityFieldDescribe( "主责人所属公司" )
	private String responsibilityCompanyName = null;
	
	@EntityFieldDescribe( "协助人姓名" )
	private String cooperateEmployeeName = null;
	
	@EntityFieldDescribe( "协助人所属组织" )
	private String cooperateOrganizationName = null;
	
	@EntityFieldDescribe( "协助人身份" )
	private String cooperateIdentity = null;
	
	@EntityFieldDescribe( "工作类别" )
	private String workType = null;
	
	@EntityFieldDescribe( "工作级别" )
	private String workLevel = null;
	
	@EntityFieldDescribe( "工作进度" )
	private Integer overallProgress = 0;
	
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
	
	private String completeDateLimitStr = null;
   
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

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
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

	public String getCooperateEmployeeName() {
		return cooperateEmployeeName;
	}

	public void setCooperateEmployeeName(String cooperateEmployeeName) {
		this.cooperateEmployeeName = cooperateEmployeeName;
	}

	public Integer getOverallProgress() {
		return overallProgress;
	}

	public void setOverallProgress(Integer overallProgress) {
		this.overallProgress = overallProgress;
	}
}