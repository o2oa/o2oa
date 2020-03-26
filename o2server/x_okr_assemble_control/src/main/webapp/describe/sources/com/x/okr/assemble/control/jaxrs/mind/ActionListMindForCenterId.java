package com.x.okr.assemble.control.jaxrs.mind;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.mind.exception.ExceptionCenterWorkNotExists;
import com.x.okr.assemble.control.jaxrs.mind.exception.ExceptionDeployedWorkListAll;
import com.x.okr.assemble.control.jaxrs.mind.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.mind.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.mind.exception.ExceptionViewableWorkList;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ActionListMindForCenterId extends BaseAction {
	protected WrapCopier<OkrCenterWorkInfo, Wo> okrCenterSimpleInfo_wrapout_copier = WrapCopierFactory
			.wo(OkrCenterWorkInfo.class, Wo.class, null, JpaObject.FieldsInvisible);
	private static  Logger logger = LoggerFactory.getLogger(ActionListMindForCenterId.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String centerId)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<Wo>();
		List<WoOkrWorkBaseSimpleInfo> wrapsWorkBaseInfoList_for_center = new ArrayList<>();
		List<WoOkrWorkBaseSimpleInfo> viewWorkBaseInfoList = new ArrayList<>();
		WoOkrWorkBaseSimpleInfo wrapOutOkrWorkBaseSimpleInfo = null;
		Wo center = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrUserCache okrUserCache = null;
		Boolean check = true;
		List<String> myWorkIds = null;
		List<String> query_statuses = new ArrayList<String>();
		query_statuses.add("正常");
		query_statuses.add("已归档");

		if (check) {
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName(effectivePerson.getDistinguishedName());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionGetOkrUserCache(e, effectivePerson.getDistinguishedName());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check && (okrUserCache == null || okrUserCache.getLoginIdentityName() == null)) {
			check = false;
			Exception exception = new ExceptionUserNoLogin(effectivePerson.getDistinguishedName());
			result.error(exception);
			// logger.error( e, effectivePerson, request, null);
		}

		if (check) {// 查询中心工作信息是否存在
			okrCenterWorkInfo = okrCenterWorkInfoService.get(centerId);
			if (okrCenterWorkInfo == null) {
				check = false;
				Exception exception = new ExceptionCenterWorkNotExists(effectivePerson.getDistinguishedName());
				result.error(exception);
				// logger.error( e, effectivePerson, request, null);
			}
		}

		if (check) {// 查询中心工作信息是否存在
			center = new Wo();
			center.setDefaultCompleteDateLimitStr(okrCenterWorkInfo.getDefaultCompleteDateLimitStr());
			center.setProcessStatus(okrCenterWorkInfo.getProcessStatus());
			center.setStatus(okrCenterWorkInfo.getStatus());
			center.setDefaultWorkType(okrCenterWorkInfo.getDefaultWorkType());
			center.setDescription(okrCenterWorkInfo.getDescription());
			center.setId(okrCenterWorkInfo.getId());
			center.setTitle(okrCenterWorkInfo.getTitle());
			center.setCreateTime(okrCenterWorkInfo.getCreateTime());
			center.setWatch(true);
		}

		if (check) {// 获取用户可以看到的所有具体工作信息（有观察者身份的）
			if (!okrUserCache.isOkrManager()) {
				try {
					myWorkIds = okrWorkPersonService.listDistinctWorkIdsWithMe(okrUserCache.getLoginIdentityName(),
							centerId);
				} catch (Exception e) {
					result.error(e);
					Exception exception = new ExceptionViewableWorkList(e, okrUserCache.getLoginIdentityName(),
							centerId);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			} else {
				try {
					myWorkIds = okrWorkBaseInfoService.listAllDeployedWorkIds(centerId, null);
				} catch (Exception e) {
					result.error(e);
					Exception exception = new ExceptionDeployedWorkListAll(e, centerId);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}

		if (check) {
			if (myWorkIds != null && !myWorkIds.isEmpty()) {
				for (String workId : myWorkIds) {

					if (workId != null && !workId.isEmpty()) {
						okrWorkBaseInfo = okrWorkBaseInfoService.get(workId);
						wrapOutOkrWorkBaseSimpleInfo = new WoOkrWorkBaseSimpleInfo();
						wrapOutOkrWorkBaseSimpleInfo.setWatch(true);
						composeWorkInfo(okrWorkBaseInfo, wrapOutOkrWorkBaseSimpleInfo);
						if (!workContain(wrapOutOkrWorkBaseSimpleInfo, viewWorkBaseInfoList)) {
							viewWorkBaseInfoList.add(wrapOutOkrWorkBaseSimpleInfo);
						}
						composeParentWork(okrWorkBaseInfo, viewWorkBaseInfoList, myWorkIds);
					}
				}
			}
		}

		if (check) {
			for (WoOkrWorkBaseSimpleInfo wrap_work : viewWorkBaseInfoList) {
				// 判断工作是否有未提交的工作汇报
				// hasNoneSubmitReport = false;
				// hasNoneSubmitReport =
				// okrWorkBaseInfoService.hasNoneSubmitReport(
				// wrap_work.getId(), "草稿", "草稿", null );
				// wrap_work.setHasNoneSubmitReport( hasNoneSubmitReport );
				if (wrap_work.getParentId() == null || wrap_work.getParentId().isEmpty()) {
					wrap_work = composeSubWork(viewWorkBaseInfoList, wrap_work);
					wrapsWorkBaseInfoList_for_center.add(wrap_work);
				}
			}
			if (wrapsWorkBaseInfoList_for_center != null && !wrapsWorkBaseInfoList_for_center.isEmpty()) {
				try {
					SortTools.asc(wrapsWorkBaseInfoList_for_center, "completeDateLimitStr");
				} catch (Exception e) {
					result.error(e);
					logger.error(e, effectivePerson, request, null);
				}
			}
			if (center != null) {
				center.setWorks(wrapsWorkBaseInfoList_for_center);

			}
		}
		result.setData(center);
		return result;
	}

	private void composeParentWork(OkrWorkBaseInfo okrWorkBaseInfo, List<WoOkrWorkBaseSimpleInfo> viewWorkBaseInfoList,
			List<String> myWorkIds) throws Exception {
		if (okrWorkBaseInfo == null) {
			return;
		}
		if (myWorkIds == null) {
			return;
		}
		if (viewWorkBaseInfoList == null) {
			viewWorkBaseInfoList = new ArrayList<WoOkrWorkBaseSimpleInfo>();
		}
		if (okrWorkBaseInfo.getParentWorkId() != null && !okrWorkBaseInfo.getParentWorkId().isEmpty()
				&& !okrWorkBaseInfo.getId().equalsIgnoreCase(okrWorkBaseInfo.getParentWorkId())) {
			OkrWorkBaseInfo parentWork = null;
			WoOkrWorkBaseSimpleInfo wrapOutOkrWorkBaseSimpleInfo = null;
			parentWork = okrWorkBaseInfoService.get(okrWorkBaseInfo.getParentWorkId());
			if (parentWork != null) {
				wrapOutOkrWorkBaseSimpleInfo = new WoOkrWorkBaseSimpleInfo();
				composeWorkInfo(parentWork, wrapOutOkrWorkBaseSimpleInfo);
				for (String id : myWorkIds) {
					if (id != null && !id.isEmpty()) {
						if (parentWork.getId().equalsIgnoreCase(id)) {
							// 是用户自己可以查看的工作, 用户可以点击开
							wrapOutOkrWorkBaseSimpleInfo.setWatch(true);
						}
					}
				}

				composeParentWork(parentWork, viewWorkBaseInfoList, myWorkIds);

				if (!workContain(wrapOutOkrWorkBaseSimpleInfo, viewWorkBaseInfoList)) {
					viewWorkBaseInfoList.add(wrapOutOkrWorkBaseSimpleInfo);
				}
			}
		}
	}

	private void composeWorkInfo(OkrWorkBaseInfo work, WoOkrWorkBaseSimpleInfo wrapOutOkrWorkBaseSimpleInfo) {
		if (work == null) {
			return;
		}
		if (wrapOutOkrWorkBaseSimpleInfo == null) {
			return;
		}
		wrapOutOkrWorkBaseSimpleInfo.setId(work.getId());
		wrapOutOkrWorkBaseSimpleInfo.setTitle(work.getTitle());
		wrapOutOkrWorkBaseSimpleInfo.setWorkProcessStatus(work.getWorkProcessStatus());
		wrapOutOkrWorkBaseSimpleInfo.setParentId(work.getParentWorkId());
		wrapOutOkrWorkBaseSimpleInfo.setIsOverTime(work.getIsOverTime());
		wrapOutOkrWorkBaseSimpleInfo.setIsCompleted(work.getIsCompleted());
		wrapOutOkrWorkBaseSimpleInfo.setOverallProgress(work.getOverallProgress());
		wrapOutOkrWorkBaseSimpleInfo.setWorkType(work.getWorkType());
		wrapOutOkrWorkBaseSimpleInfo.setCompleteDateLimitStr(work.getCompleteDateLimitStr());
		wrapOutOkrWorkBaseSimpleInfo.setResponsibilityUnitName(work.getResponsibilityUnitName());
		wrapOutOkrWorkBaseSimpleInfo.setResponsibilityIdentity(work.getResponsibilityIdentity());
		wrapOutOkrWorkBaseSimpleInfo.setResponsibilityEmployeeName(work.getResponsibilityEmployeeName());
		wrapOutOkrWorkBaseSimpleInfo.setCooperateUnitNameList(work.getCooperateUnitNameList());
		wrapOutOkrWorkBaseSimpleInfo.setCooperateEmployeeNameList(work.getCooperateEmployeeNameList());
		wrapOutOkrWorkBaseSimpleInfo.setCooperateIdentityList(work.getCooperateIdentityList());
	}

	private Boolean workContain(WoOkrWorkBaseSimpleInfo wrapOutOkrWorkBaseSimpleInfo,
			List<WoOkrWorkBaseSimpleInfo> viewWorkBaseInfoList) {
		if (wrapOutOkrWorkBaseSimpleInfo == null) {
			return true;
		}
		if (viewWorkBaseInfoList == null) {
			viewWorkBaseInfoList = new ArrayList<WoOkrWorkBaseSimpleInfo>();
			return false;
		}
		for (WoOkrWorkBaseSimpleInfo info : viewWorkBaseInfoList) {
			if (info.getId().equalsIgnoreCase(wrapOutOkrWorkBaseSimpleInfo.getId())) {
				return true;
			}
		}
		return false;
	}

	private WoOkrWorkBaseSimpleInfo composeSubWork(List<WoOkrWorkBaseSimpleInfo> viewWorkBaseInfoList,
			WoOkrWorkBaseSimpleInfo wrap_work) {
		if (viewWorkBaseInfoList != null && !viewWorkBaseInfoList.isEmpty()) {
			for (WoOkrWorkBaseSimpleInfo work : viewWorkBaseInfoList) {
				if (work.getParentId() != null && !work.getParentId().isEmpty()
						&& work.getParentId().equalsIgnoreCase(wrap_work.getId())) {
					// 说明该工作是wrap_work的下级工作
					work = composeSubWork(viewWorkBaseInfoList, work);
					wrap_work.addNewSubWorkBaseInfo(work);
				}
			}
		}
		return wrap_work;
	}

	public static class Wo {

		@FieldDescribe("中心工作ID")
		private String id = null;

		@FieldDescribe("中心标题")
		private String title = null;

		@FieldDescribe("部署者姓名")
		private String deployerName = null;

		@FieldDescribe("部署者身份")
		private String deployerIdentity = null;

		@FieldDescribe("部署者所属组织")
		private String deployerUnitName = null;

		@FieldDescribe("部署者所属顶层组织")
		private String deployerTopUnitName = null;

		@FieldDescribe("审核者姓名")
		private String auditLeaderName = null;

		@FieldDescribe("审核者身份")
		private String auditLeaderIdentity = null;

		@FieldDescribe("中心工作处理状态：草稿|待审核|待确认|执行中|已完成|已撤消")
		private String processStatus = "草稿";

		@FieldDescribe("中心工作默认完成日期-字符串，显示用：yyyy-mm-dd")
		private String defaultCompleteDateLimitStr = null;

		@FieldDescribe("中心工作默认工作类别")
		private String defaultWorkType = null;

		@FieldDescribe("中心工作默认工作级别")
		private String defaultWorkLevel = null;

		@FieldDescribe("中心工作默认阅知领导(可多值，显示用)")
		private String defaultLeader = null;

		@FieldDescribe("中心工作默认阅知领导身份(可多值，计算组织和顶层组织用)")
		private String defaultLeaderIdentity = null;

		@FieldDescribe("工作汇报审批领导(可多值，显示用)")
		private String reportAuditLeaderName = null;

		@FieldDescribe("工作汇报审批领导身份(可多值，计算组织和顶层组织用)")
		private String reportAuditLeaderIdentity = null;

		@FieldDescribe("中心工作是否需要审核")
		private Boolean isNeedAudit = false;

		@FieldDescribe("处理状态：正常|已删除")
		private String status = "正常";

		@FieldDescribe("中心工作描述")
		private String description = null;

		@FieldDescribe("中心工作创建时间")
		private Date createTime = null;

		@FieldDescribe("中心工作包括的工作列表")
		private List<WoOkrWorkBaseSimpleInfo> works = null;

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

		public List<WoOkrWorkBaseSimpleInfo> getWorks() {
			return works;
		}

		public void setWorks(List<WoOkrWorkBaseSimpleInfo> works) {
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

	public static class WoOkrWorkBaseSimpleInfo {

		public static List<String> Excludes = new ArrayList<String>();

		@FieldDescribe("子工作信息列表")
		private List<WoOkrWorkBaseSimpleInfo> subWorks = null;

		@FieldDescribe("工作ID")
		private String id = null;

		@FieldDescribe("上级工作ID")
		private String parentId = null;

		@FieldDescribe("工作标题")
		private String title = null;

		@FieldDescribe("中心工作ID")
		private String centerId = null;

		@FieldDescribe("中心工作标题")
		private String centerTitle = null;

		@FieldDescribe("主责人姓名")
		private String responsibilityEmployeeName = null;

		@FieldDescribe("主责人身份")
		private String responsibilityIdentity = null;

		@FieldDescribe("主责人所属组织")
		private String responsibilityUnitName = null;

		@FieldDescribe("主责人所属顶层组织")
		private String responsibilityTopUnitName = null;

		@FieldDescribe("协助人姓名")
		private List<String> cooperateEmployeeNameList = null;

		@FieldDescribe("协助人所属组织")
		private List<String> cooperateUnitNameList = null;

		@FieldDescribe("协助人身份")
		private List<String> cooperateIdentityList = null;

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

		public List<String> getCooperateEmployeeNameList() {
			return cooperateEmployeeNameList;
		}

		public List<String> getCooperateUnitNameList() {
			return cooperateUnitNameList;
		}

		public List<String> getCooperateIdentityList() {
			return cooperateIdentityList;
		}

		public void setCooperateEmployeeNameList(List<String> cooperateEmployeeNameList) {
			this.cooperateEmployeeNameList = cooperateEmployeeNameList;
		}

		public void setCooperateUnitNameList(List<String> cooperateUnitNameList) {
			this.cooperateUnitNameList = cooperateUnitNameList;
		}

		public void setCooperateIdentityList(List<String> cooperateIdentityList) {
			this.cooperateIdentityList = cooperateIdentityList;
		}

		public Integer getOverallProgress() {
			return overallProgress;
		}

		public void setOverallProgress(Integer overallProgress) {
			this.overallProgress = overallProgress;
		}
	}
}