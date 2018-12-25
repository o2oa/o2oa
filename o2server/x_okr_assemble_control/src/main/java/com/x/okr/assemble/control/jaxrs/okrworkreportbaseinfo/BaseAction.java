package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoId;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.service.OkrCenterWorkQueryService;
import com.x.okr.assemble.control.service.OkrConfigSystemService;
import com.x.okr.assemble.control.service.OkrConfigWorkTypeService;
import com.x.okr.assemble.control.service.OkrTaskService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.assemble.control.service.OkrWorkAuthorizeRecordService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkDetailInfoService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.assemble.control.service.OkrWorkReportDetailInfoService;
import com.x.okr.assemble.control.service.OkrWorkReportFlowService;
import com.x.okr.assemble.control.service.OkrWorkReportOperationService;
import com.x.okr.assemble.control.service.OkrWorkReportPersonLinkService;
import com.x.okr.assemble.control.service.OkrWorkReportProcessLogService;
import com.x.okr.assemble.control.service.OkrWorkReportQueryService;
import com.x.okr.assemble.control.service.OkrWorkReportTaskCollectService;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportProcessLog;

public class BaseAction extends StandardJaxrsAction {

	protected OkrWorkReportFlowService okrWorkReportFlowService = new OkrWorkReportFlowService();
	protected OkrWorkReportOperationService okrWorkReportOperationService = new OkrWorkReportOperationService();
	protected OkrWorkReportQueryService okrWorkReportQueryService = new OkrWorkReportQueryService();
	protected OkrWorkReportDetailInfoService okrWorkReportDetailInfoService = new OkrWorkReportDetailInfoService();
	protected OkrWorkReportPersonLinkService okrWorkReportPersonLinkService = new OkrWorkReportPersonLinkService();
	protected OkrWorkReportProcessLogService okrWorkReportProcessLogService = new OkrWorkReportProcessLogService();
	protected OkrCenterWorkQueryService okrCenterWorkInfoService = new OkrCenterWorkQueryService();
	protected OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
	protected OkrWorkDetailInfoService okrWorkDetailInfoService = new OkrWorkDetailInfoService();
	protected OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	protected OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();
	protected OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	protected OkrTaskService okrTaskService = new OkrTaskService();
	protected OkrConfigWorkTypeService okrConfigWorkTypeService = new OkrConfigWorkTypeService();
	protected OkrWorkReportTaskCollectService okrWorkReportTaskCollectService = new OkrWorkReportTaskCollectService();
	protected OkrWorkAuthorizeRecordService okrWorkAuthorizeRecordService = new OkrWorkAuthorizeRecordService();
	protected DateOperation dateOperation = new DateOperation();

	public static class WoOkrCenterWorkInfo extends OkrCenterWorkInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<OkrCenterWorkInfo, WoOkrCenterWorkInfo> copier = WrapCopierFactory
				.wo(OkrCenterWorkInfo.class, WoOkrCenterWorkInfo.class, null, JpaObject.FieldsInvisible);

		@FieldDescribe("所有的工作列表")
		private List<WoOkrWorkBaseInfo> works = null;

		@FieldDescribe("所有的工作类别列表")
		private List<WoOkrWorkType> workTypes = null;

		@FieldDescribe("用户可以对工作进行的操作(多值):CREATEWORK|IMPORTWORK|DEPLOY|ARCHIVE|CLOSE|DELETE")
		private List<String> operation = null;

		@FieldDescribe("是否为新创建的草稿信息")
		private Boolean isNew = true;

		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public List<WoOkrWorkBaseInfo> getWorks() {
			return works;
		}

		public void setWorks(List<WoOkrWorkBaseInfo> works) {
			this.works = works;
		}

		public List<WoOkrWorkType> getWorkTypes() {
			return workTypes;
		}

		public void setWorkTypes(List<WoOkrWorkType> workTypes) {
			this.workTypes = workTypes;
		}

		public List<String> getOperation() {
			return operation;
		}

		public void setOperation(List<String> operation) {
			this.operation = operation;
		}

		public Boolean getIsNew() {
			return isNew;
		}

		public void setIsNew(Boolean isNew) {
			this.isNew = isNew;
		}

	}

	public static class WoOkrCenterWorkViewInfo {

		public static WrapCopier<OkrCenterWorkInfo, WoOkrCenterWorkViewInfo> copier = WrapCopierFactory
				.wo(OkrCenterWorkInfo.class, WoOkrCenterWorkViewInfo.class, null, JpaObject.FieldsInvisible);

		@FieldDescribe("中心工作ID")
		private String id = "";

		@FieldDescribe("中心标题")
		private String title = "";

		@FieldDescribe("部署者姓名")
		private String deployerName = "";

		@FieldDescribe("部署者身份")
		private String deployerIdentity = "";

		@FieldDescribe("部署者所属组织")
		private String deployerUnitName = "";

		@FieldDescribe("部署者所属顶层组织")
		private String deployerTopUnitName = "";

		@FieldDescribe("审核者姓名，多值")
		private List<String> auditLeaderNameList = null;

		@FieldDescribe("审核者身份，多值")
		private List<String> auditLeaderIdentityList = null;

		@FieldDescribe("中心工作处理状态：草稿|待审核|待确认|执行中|已完成|已撤消")
		private String processStatus = "草稿";

		@FieldDescribe("中心工作默认完成日期-字符串，显示用：yyyy-mm-dd")
		private String defaultCompleteDateLimitStr = "";

		@FieldDescribe("中心工作默认工作类别")
		private String defaultWorkType = "";

		@FieldDescribe("中心工作默认工作级别")
		private String defaultWorkLevel = "";

		@FieldDescribe("中心工作默认阅知领导(可多值，显示用)")
		private List<String> defaultLeaderList = null;

		@FieldDescribe("中心工作默认阅知领导身份(多值)")
		private List<String> defaultLeaderIdentityList = null;

		@FieldDescribe("工作汇报审批领导(多值)")
		private List<String> reportAuditLeaderNameList = null;

		@FieldDescribe("工作汇报审批领导身份(多值)")
		private List<String> reportAuditLeaderIdentityList = null;

		@FieldDescribe("中心工作是否需要审核")
		private Boolean isNeedAudit = false;

		@FieldDescribe("处理状态：正常|已删除")
		private String status = "正常";

		@FieldDescribe("中心工作描述")
		private String description = "";

		@FieldDescribe("工作处理职责身份(多值): VIEW(观察者)|DEPLOY(部署者)|RESPONSIBILITY(责任者)|COOPERATE(协助者)|READ(阅知者)|REPORTAUDIT(汇报审核者)")
		private List<String> workProcessIdentity = null;

		@FieldDescribe("用户可以对工作进行的操作(多值):VIEW|EDIT|DELETE")
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

		public String getDeployerUnitName() {
			return deployerUnitName;
		}

		public void setDeployerUnitName(String deployerUnitName) {
			this.deployerUnitName = deployerUnitName;
		}

		public String getDeployerTopUnitName() {
			return deployerTopUnitName;
		}

		public void setDeployerTopUnitName(String deployerTopUnitName) {
			this.deployerTopUnitName = deployerTopUnitName;
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

		public List<String> getAuditLeaderNameList() {
			return auditLeaderNameList == null?new ArrayList<>():auditLeaderNameList;
		}

		public List<String> getAuditLeaderIdentityList() {
			return auditLeaderIdentityList == null?new ArrayList<>():auditLeaderIdentityList;
		}

		public List<String> getDefaultLeaderList() {
			return defaultLeaderList == null?new ArrayList<>():defaultLeaderList;
		}

		public List<String> getDefaultLeaderIdentityList() {
			return defaultLeaderIdentityList == null?new ArrayList<>():defaultLeaderIdentityList;
		}

		public List<String> getReportAuditLeaderNameList() {
			return reportAuditLeaderNameList == null?new ArrayList<>():reportAuditLeaderNameList;
		}

		public List<String> getReportAuditLeaderIdentityList() {
			return reportAuditLeaderIdentityList == null?new ArrayList<>():reportAuditLeaderIdentityList;
		}

		public void setAuditLeaderNameList(List<String> auditLeaderNameList) {
			this.auditLeaderNameList = auditLeaderNameList;
		}

		public void setAuditLeaderIdentityList(List<String> auditLeaderIdentityList) {
			this.auditLeaderIdentityList = auditLeaderIdentityList;
		}

		public void setDefaultLeaderList(List<String> defaultLeaderList) {
			this.defaultLeaderList = defaultLeaderList;
		}

		public void setDefaultLeaderIdentityList(List<String> defaultLeaderIdentityList) {
			this.defaultLeaderIdentityList = defaultLeaderIdentityList;
		}

		public void setReportAuditLeaderNameList(List<String> reportAuditLeaderNameList) {
			this.reportAuditLeaderNameList = reportAuditLeaderNameList;
		}

		public void setReportAuditLeaderIdentityList(List<String> reportAuditLeaderIdentityList) {
			this.reportAuditLeaderIdentityList = reportAuditLeaderIdentityList;
		}
	}

	public static class WoOkrWorkBaseInfo extends OkrWorkBaseInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<OkrWorkBaseInfo, WoOkrWorkBaseInfo> copier = WrapCopierFactory
				.wo(OkrWorkBaseInfo.class, WoOkrWorkBaseInfo.class, null, JpaObject.FieldsInvisible);

		private List<WoOkrWorkBaseInfo> subWrapOutOkrWorkBaseInfos = null;

		private List<WoOkrWorkAuthorizeRecord> okrWorkAuthorizeRecords = null;

		private WoOkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;

		private String workOutType = "SUBWORK";

		private String workDetail = null;

		private String dutyDescription = null;

		private String landmarkDescription = null;

		private String majorIssuesDescription = null;

		private String progressAction = null;

		private String progressPlan = null;

		private String resultDescription = null;

		private Boolean hasNoneSubmitReport = false;

		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public List<WoOkrWorkBaseInfo> getSubWrapOutOkrWorkBaseInfos() {
			return subWrapOutOkrWorkBaseInfos;
		}

		public void setSubWrapOutOkrWorkBaseInfos(List<WoOkrWorkBaseInfo> subWrapOutOkrWorkBaseInfos) {
			this.subWrapOutOkrWorkBaseInfos = subWrapOutOkrWorkBaseInfos;
		}

		public void addNewSubWorkBaseInfo(WoOkrWorkBaseInfo workBaseInfo) {
			if (this.subWrapOutOkrWorkBaseInfos == null) {
				this.subWrapOutOkrWorkBaseInfos = new ArrayList<WoOkrWorkBaseInfo>();
			}
			if (!subWrapOutOkrWorkBaseInfos.contains(workBaseInfo)) {
				subWrapOutOkrWorkBaseInfos.add(workBaseInfo);
			}
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

		/**
		 * 判断是父级工作还是子工作
		 * 
		 * @return
		 */
		public String getWorkOutType() {
			return workOutType;
		}

		/**
		 * 判断是父级工作还是子工作
		 * 
		 * @param workOutType
		 */
		public void setWorkOutType(String workOutType) {
			this.workOutType = workOutType;
		}

		public List<WoOkrWorkAuthorizeRecord> getOkrWorkAuthorizeRecords() {
			return okrWorkAuthorizeRecords;
		}

		public void setOkrWorkAuthorizeRecords(List<WoOkrWorkAuthorizeRecord> okrWorkAuthorizeRecords) {
			this.okrWorkAuthorizeRecords = okrWorkAuthorizeRecords;
		}

		public WoOkrWorkAuthorizeRecord getOkrWorkAuthorizeRecord() {
			return okrWorkAuthorizeRecord;
		}

		public void setOkrWorkAuthorizeRecord(WoOkrWorkAuthorizeRecord okrWorkAuthorizeRecord) {
			this.okrWorkAuthorizeRecord = okrWorkAuthorizeRecord;
		}

		public Boolean getHasNoneSubmitReport() {
			return hasNoneSubmitReport;
		}

		public void setHasNoneSubmitReport(Boolean hasNoneSubmitReport) {
			this.hasNoneSubmitReport = hasNoneSubmitReport;
		}

	}

	public static class WoOkrWorkBaseSimpleInfo {

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<OkrWorkBaseInfo, WoOkrWorkBaseSimpleInfo> copier = WrapCopierFactory
				.wo(OkrWorkBaseInfo.class, WoOkrWorkBaseSimpleInfo.class, null, JpaObject.FieldsInvisible);

		@FieldDescribe("子工作信息列表")
		private List<WoOkrWorkBaseSimpleInfo> subWorks = null;

		@FieldDescribe("工作ID")
		private String id = null;

		@FieldDescribe("上级工作ID")
		private String parentWorkId = null;

		@FieldDescribe("工作标题")
		private String title = null;

		@FieldDescribe("中心工作ID")
		private String centerId = null;

		@FieldDescribe("中心工作标题")
		private String centerTitle = null;

		@FieldDescribe("部署者姓名")
		private String deployerName = null;

		@FieldDescribe("部署者身份")
		private String deployerIdentity = null;

		@FieldDescribe("部署者所属组织")
		private String deployerUnitName = null;

		@FieldDescribe("部署者所属顶层组织")
		private String deployerTopUnitName = null;

		@FieldDescribe("主责人姓名")
		private String responsibilityEmployeeName = null;

		@FieldDescribe("主责人身份")
		private String responsibilityIdentity = null;

		@FieldDescribe("主责人所属组织")
		private String responsibilityUnitName = null;

		@FieldDescribe("主责人所属顶层组织")
		private String responsibilityTopUnitName = null;

		@FieldDescribe("协助人姓名，多值")
		private List<String> cooperateEmployeeNameList = null;

		@FieldDescribe("协助人身份，多值")
		private List<String> cooperateIdentityList = null;

		@FieldDescribe("协助人所属组织，多值")
		private List<String> cooperateUnitNameList = null;

		@FieldDescribe("协助人所属顶层组织，多值")
		private List<String> cooperateTopUnitNameList = null;

		@FieldDescribe("阅知领导身份，多值")
		private List<String> readLeaderIdentityList = null;

		@FieldDescribe("阅知领导，多值")
		private List<String> readLeaderNameList = null;

		@FieldDescribe("阅知领导所属组织，多值")
		private List<String> readLeaderUnitNameList = null;

		@FieldDescribe("阅知领导所属顶层组织，多值")
		private List<String> readLeaderTopUnitNameList = null;

		@FieldDescribe("工作类别")
		private String workType = null;

		@FieldDescribe("工作级别")
		private String workLevel = null;

		@FieldDescribe("工作进度")
		private Integer overallProgress = 0;

		@FieldDescribe("工作处理状态：草稿|待确认|执行中|已超期|已完成|已撤消")
		private String workProcessStatus = "草稿";

		@FieldDescribe("工作是否已超期")
		private Boolean isOverTime = false;

		@FieldDescribe("工作是否已完成")
		private Boolean isCompleted = false;

		@FieldDescribe("工作详细描述, 事项分解")
		private String workDetail = null;

		@FieldDescribe("职责描述")
		private String dutyDescription = null;

		@FieldDescribe("里程碑标志说明")
		private String landmarkDescription = null;

		@FieldDescribe("重点事项说明")
		private String majorIssuesDescription = null;

		@FieldDescribe("具体行动举措")
		private String progressAction = null;

		@FieldDescribe("进展计划时限说明")
		private String progressPlan = null;

		@FieldDescribe("交付成果说明")
		private String resultDescription = null;

		@FieldDescribe("是否可以查看工作详情")
		private Boolean watch = false;

		@FieldDescribe("完成时限")
		private String completeDateLimitStr = null;

		@FieldDescribe("工作处理职责身份(多值): AUTHORZE(授权中)|TACKBACK(授权收回)|AUTHORIZECANCEL(授权失效)|VIEW(观察者)|RESPONSIBILITY(责任者)|COOPERATE(协助者)|READ(阅知者)")
		private List<String> workProcessIdentity = null;

		@FieldDescribe("用户可以对工作进行的操作(多值):VIEW|EDIT|SPLIT|AUTHORIZE|TACKBACK|REPORT|DELETE|")
		private List<String> operation = null;

		@FieldDescribe("工作信息状态：正常|已删除|已归档")
		private String status = "正常";

		@FieldDescribe("标识工作信息是具体工作,还是中心工作：WORK|CENTER")
		private String workOrCenter = "WORK";

		@FieldDescribe("标识工作信息是否已经被拆解过了,是否存在下级工作信息")
		private Boolean hasSubWorks = false;

		@FieldDescribe("归档日期")
		private Date archiveDate = null;

		@FieldDescribe("完成日期日期")
		private Date completeTime = null;

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

		public List<WoOkrWorkBaseSimpleInfo> getSubWorks() {
			return subWorks;
		}

		public void setSubWorks(List<WoOkrWorkBaseSimpleInfo> subWorks) {
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

		public String getResponsibilityUnitName() {
			return responsibilityUnitName;
		}

		public void setResponsibilityUnitName(String responsibilityUnitName) {
			this.responsibilityUnitName = responsibilityUnitName;
		}

		public String getResponsibilityTopUnitName() {
			return responsibilityTopUnitName;
		}

		public void setResponsibilityTopUnitName(String responsibilityTopUnitName) {
			this.responsibilityTopUnitName = responsibilityTopUnitName;
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

		public Integer getOverallProgress() {
			return overallProgress;
		}

		public void setOverallProgress(Integer overallProgress) {
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

		public void addNewSubWorkBaseInfo(WoOkrWorkBaseSimpleInfo work) {
			if (subWorks == null) {
				subWorks = new ArrayList<WoOkrWorkBaseSimpleInfo>();
			}
			subWorks.add(work);
		}

		public String getCompleteDateLimitStr() {
			return completeDateLimitStr;
		}

		public void setCompleteDateLimitStr(String completeDateLimitStr) {
			this.completeDateLimitStr = completeDateLimitStr;
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

		public String getDeployerUnitName() {
			return deployerUnitName;
		}

		public void setDeployerUnitName(String deployerUnitName) {
			this.deployerUnitName = deployerUnitName;
		}

		public String getDeployerTopUnitName() {
			return deployerTopUnitName;
		}

		public void setDeployerTopUnitName(String deployerTopUnitName) {
			this.deployerTopUnitName = deployerTopUnitName;
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

		public Date getArchiveDate() {
			return archiveDate;
		}

		public Date getCompleteTime() {
			return completeTime;
		}

		public void setArchiveDate(Date archiveDate) {
			this.archiveDate = archiveDate;
		}

		public void setCompleteTime(Date completeTime) {
			this.completeTime = completeTime;
		}

		public List<String> getCooperateEmployeeNameList() {
			return cooperateEmployeeNameList==null?new ArrayList<>():cooperateEmployeeNameList;
		}

		public List<String> getCooperateIdentityList() {
			return cooperateIdentityList==null?new ArrayList<>():cooperateIdentityList;
		}

		public List<String> getCooperateUnitNameList() {
			return cooperateUnitNameList==null?new ArrayList<>():cooperateUnitNameList;
		}

		public List<String> getCooperateTopUnitNameList() {
			return cooperateTopUnitNameList==null?new ArrayList<>():cooperateTopUnitNameList;
		}

		public List<String> getReadLeaderIdentityList() {
			return readLeaderIdentityList==null?new ArrayList<>():readLeaderIdentityList;
		}

		public List<String> getReadLeaderNameList() {
			return readLeaderNameList==null?new ArrayList<>():readLeaderNameList;
		}

		public List<String> getReadLeaderUnitNameList() {
			return readLeaderUnitNameList==null?new ArrayList<>():readLeaderUnitNameList;
		}

		public List<String> getReadLeaderTopUnitNameList() {
			return readLeaderTopUnitNameList==null?new ArrayList<>():readLeaderTopUnitNameList;
		}

		public void setCooperateEmployeeNameList(List<String> cooperateEmployeeNameList) {
			this.cooperateEmployeeNameList = cooperateEmployeeNameList;
		}

		public void setCooperateIdentityList(List<String> cooperateIdentityList) {
			this.cooperateIdentityList = cooperateIdentityList;
		}

		public void setCooperateUnitNameList(List<String> cooperateUnitNameList) {
			this.cooperateUnitNameList = cooperateUnitNameList;
		}

		public void setCooperateTopUnitNameList(List<String> cooperateTopUnitNameList) {
			this.cooperateTopUnitNameList = cooperateTopUnitNameList;
		}

		public void setReadLeaderIdentityList(List<String> readLeaderIdentityList) {
			this.readLeaderIdentityList = readLeaderIdentityList;
		}

		public void setReadLeaderNameList(List<String> readLeaderNameList) {
			this.readLeaderNameList = readLeaderNameList;
		}

		public void setReadLeaderUnitNameList(List<String> readLeaderUnitNameList) {
			this.readLeaderUnitNameList = readLeaderUnitNameList;
		}

		public void setReadLeaderTopUnitNameList(List<String> readLeaderTopUnitNameList) {
			this.readLeaderTopUnitNameList = readLeaderTopUnitNameList;
		}
		
	}

	public static class WoOkrWorkAuthorizeRecord extends OkrWorkAuthorizeRecord {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<OkrWorkAuthorizeRecord, WoOkrWorkAuthorizeRecord> copier = WrapCopierFactory
				.wo(OkrWorkAuthorizeRecord.class, WoOkrWorkAuthorizeRecord.class, null, JpaObject.FieldsInvisible);

		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
	}

	public static class WoOkrWorkType {

		@FieldDescribe("工作类别ID")
		private String id = null;

		@FieldDescribe("工作类别名称")
		private String workTypeName = null;

		@FieldDescribe("工作类别排序号")
		private Integer orderNumber = null;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public WoOkrWorkType() {

		}

		public WoOkrWorkType(String id, String name, Integer orderNumber) {
			this.id = id;
			this.workTypeName = name;
			this.orderNumber = orderNumber;
		}

		public String getWorkTypeName() {
			return workTypeName;
		}

		public void setWorkTypeName(String workTypeName) {
			this.workTypeName = workTypeName;
		}

		public Integer getOrderNumber() {
			return orderNumber;
		}

		public void setOrderNumber(Integer orderNumber) {
			this.orderNumber = orderNumber;
		}
	}

	public static class WoOkrAttachmentFileInfo extends OkrAttachmentFileInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<OkrAttachmentFileInfo, WoOkrAttachmentFileInfo> copier = WrapCopierFactory
				.wo(OkrAttachmentFileInfo.class, WoOkrAttachmentFileInfo.class, null, JpaObject.FieldsInvisible);

		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
	}

	public static class WoOkrWorkReportBaseInfo extends OkrWorkReportBaseInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<OkrWorkReportBaseInfo, WoOkrWorkReportBaseInfo> copier = WrapCopierFactory
				.wo(OkrWorkReportBaseInfo.class, WoOkrWorkReportBaseInfo.class, null, JpaObject.FieldsInvisible);
		private Boolean isReporter = false;

		private Boolean isWorkAdmin = false;

		private Boolean isReadLeader = false;

		private Boolean isCreator = false;

		private Boolean needReportProgress = false;

		/**
		 * 管理员督办信息
		 */
		private String adminSuperviseInfo = "";

		private String workPointAndRequirements = "";
		/**
		 * 填写汇报时填写的具体进展描述信息
		 */
		private String progressDescription = "";
		/**
		 * 下一步工作计划信息
		 */
		private String workPlan = "";
		/**
		 * 说明备注信息
		 */
		private String memo = "";

		private List<WoOkrWorkReportProcessLog> processLogs = null;

		private WoOkrWorkBaseInfo workInfo = null;

		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public Boolean getIsReporter() {
			return isReporter;
		}

		public void setIsReporter(Boolean isReporter) {
			this.isReporter = isReporter;
		}

		public Boolean getIsWorkAdmin() {
			return isWorkAdmin;
		}

		public void setIsWorkAdmin(Boolean isWorkAdmin) {
			this.isWorkAdmin = isWorkAdmin;
		}

		public Boolean getIsReadLeader() {
			return isReadLeader;
		}

		public void setIsReadLeader(Boolean isReadLeader) {
			this.isReadLeader = isReadLeader;
		}

		public Boolean getIsCreator() {
			return isCreator;
		}

		public void setIsCreator(Boolean isCreator) {
			this.isCreator = isCreator;
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

		public String getMemo() {
			return memo;
		}

		public void setMemo(String memo) {
			this.memo = memo;
		}

		public String getWorkPointAndRequirements() {
			return workPointAndRequirements;
		}

		public void setWorkPointAndRequirements(String workPointAndRequirements) {
			this.workPointAndRequirements = workPointAndRequirements;
		}

		public List<WoOkrWorkReportProcessLog> getProcessLogs() {
			return processLogs;
		}

		public void setProcessLogs(List<WoOkrWorkReportProcessLog> processLogs) {
			this.processLogs = processLogs;
		}

		public String getAdminSuperviseInfo() {
			return adminSuperviseInfo;
		}

		public void setAdminSuperviseInfo(String adminSuperviseInfo) {
			this.adminSuperviseInfo = adminSuperviseInfo;
		}

		public WoOkrWorkBaseInfo getWorkInfo() {
			return workInfo;
		}

		public void setWorkInfo(WoOkrWorkBaseInfo workInfo) {
			this.workInfo = workInfo;
		}

		public Boolean getNeedReportProgress() {
			return needReportProgress;
		}

		public void setNeedReportProgress(Boolean needReportProgress) {
			this.needReportProgress = needReportProgress;
		}

	}

	public static class WoOkrWorkReportBaseSimpleInfo {

		public static WrapCopier<OkrWorkReportBaseInfo, WoOkrWorkReportBaseSimpleInfo> copier = WrapCopierFactory
				.wo(OkrWorkReportBaseInfo.class, WoOkrWorkReportBaseSimpleInfo.class, null,
						JpaObject.FieldsInvisible);

		@FieldDescribe("汇报ID.")
		private String id;

		@FieldDescribe("工作汇报标题")
		private String title = null;

		@FieldDescribe("工作汇报短标题")
		private String shortTitle = null;

		@FieldDescribe("工作汇报当前环节")
		private String activityName = "草稿";

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getShortTitle() {
			return shortTitle;
		}

		public void setShortTitle(String shortTitle) {
			this.shortTitle = shortTitle;
		}

		public String getActivityName() {
			return activityName;
		}

		public void setActivityName(String activityName) {
			this.activityName = activityName;
		}
	}

	public static class WoOkrWorkDeployAuthorizeRecord {

		@FieldDescribe("工作ID")
		private String workId = "";

		@FieldDescribe("工作标题")
		private String workTitle = "";

		@FieldDescribe("工作部署|授权操作者身份")
		private String source = "";

		@FieldDescribe("工作部署|授权操作接收者身份")
		private String target;

		@FieldDescribe("工作部署|授权操作时间")
		private String operationTime;

		@FieldDescribe("工作部署|授权操作类型:授权|收回|部署")
		private String operationTypeCN;

		@FieldDescribe("DEPLOY|AUTHORIZE|TACKBACK")
		private String operationType;

		@FieldDescribe("工作部署|授权操作意见")
		private String opinion;

		private String description = "";

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public String getTarget() {
			return target;
		}

		public void setTarget(String target) {
			this.target = target;
		}

		public String getOperationTime() {
			return operationTime;
		}

		public void setOperationTime(String operationTime) {
			this.operationTime = operationTime;
		}

		public String getOperationType() {
			return operationType;
		}

		public void setOperationType(String operationType) {
			this.operationType = operationType;
		}

		public String getOpinion() {
			return opinion;
		}

		public void setOpinion(String opinion) {
			this.opinion = opinion;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

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

		public String getOperationTypeCN() {
			return operationTypeCN;
		}

		public void setOperationTypeCN(String operationTypeCN) {
			this.operationTypeCN = operationTypeCN;
		}
	}

	public static class WoOkrWorkReportProcessLog extends OkrWorkReportProcessLog {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<OkrWorkReportProcessLog, WoOkrWorkReportProcessLog> copier = WrapCopierFactory.wo(
				OkrWorkReportProcessLog.class, WoOkrWorkReportProcessLog.class, null,
				JpaObject.FieldsInvisible);

		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}

	public static class Wo extends WoId {
		public Wo(String id) {
			this.setId(id);
		}
	}

	public static class WiOkrWorkReportBaseInfo extends OkrWorkReportBaseInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

		private String workPointAndRequirements = "";
		/**
		 * 填写汇报时填写的具体进展描述信息
		 */
		private String progressDescription = "";
		/**
		 * 下一步工作计划信息
		 */
		private String workPlan = "";
		/**
		 * 管理员督办信息
		 */
		private String adminSuperviseInfo = "";
		/**
		 * 说明备注信息
		 */
		private String memo = "";

		private String opinion = "";

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

		public String getMemo() {
			return memo;
		}

		public void setMemo(String memo) {
			this.memo = memo;
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

		public String getOpinion() {
			return opinion;
		}

		public void setOpinion(String opinion) {
			this.opinion = opinion;
		}
	}

	protected void sendWorkReportReadTask(OkrWorkReportBaseInfo okrWorkReportBaseInfo, String userIdentity)
			throws Exception {
		String userUnitName = null;
		String userTopUnitName = null;
		String personName = null;
		List<String> ids = okrTaskService.listIdsByTargetActivityAndObjId("汇报确认", okrWorkReportBaseInfo.getId(), "汇报确认",
				userIdentity);
		if (ids == null || ids.isEmpty()) {
			personName = okrUserManagerService.getPersonNameByIdentity(userIdentity);
			if (personName != null) {
				userUnitName = okrUserManagerService.getUnitNameByIdentity(userIdentity);
				userTopUnitName = okrUserManagerService.getTopUnitNameByIdentity(userIdentity);
				okrWorkReportFlowService.addReportConfirmReader(okrWorkReportBaseInfo, userIdentity, personName,
						userUnitName, userTopUnitName);
			}
		}
	}
}
