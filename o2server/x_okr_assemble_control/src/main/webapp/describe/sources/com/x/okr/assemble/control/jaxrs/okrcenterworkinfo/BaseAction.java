package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.service.OkrCenterWorkOperationService;
import com.x.okr.assemble.control.service.OkrCenterWorkQueryService;
import com.x.okr.assemble.control.service.OkrConfigSystemService;
import com.x.okr.assemble.control.service.OkrConfigWorkTypeService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.assemble.control.service.OkrWorkPersonSearchService;
import com.x.okr.assemble.control.service.OkrWorkProcessIdentityService;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;

public class BaseAction extends StandardJaxrsAction {

	private static  Logger logger = LoggerFactory.getLogger(BaseAction.class);

	protected OkrUserManagerService userManagerService = new OkrUserManagerService();
	protected OkrWorkProcessIdentityService okrWorkProcessIdentityService = new OkrWorkProcessIdentityService();
	protected OkrCenterWorkQueryService okrCenterWorkQueryService = new OkrCenterWorkQueryService();
	protected OkrWorkPersonSearchService okrWorkPersonSearchService = new OkrWorkPersonSearchService();
	protected OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	protected OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	protected OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();
	protected OkrConfigWorkTypeService okrConfigWorkTypeService = new OkrConfigWorkTypeService();
	protected DateOperation dateOperation = new DateOperation();
	protected OkrCenterWorkOperationService okrCenterWorkOperationService = new OkrCenterWorkOperationService();

	protected OkrUserCache checkUserLogin(String name) {
		OkrUserCache okrUserCache = null;
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName(name);
		} catch (Exception e) {
			logger.warn("system get login indentity with person name got an exception");
			logger.error(e);
			return null;
		}
		if (okrUserCache == null || okrUserCache.getLoginIdentityName() == null) {
			return null;
		}
		if (okrUserCache.getLoginUserName() == null) {
			return null;
		}
		if (okrUserCache.getLoginUserUnitName() == null) {
			return null;
		}
		if (okrUserCache.getLoginUserTopUnitName() == null) {
			return null;
		}
		return okrUserCache;
	}

	public static class Wo extends OkrCenterWorkInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<OkrCenterWorkInfo, Wo> copier = WrapCopierFactory.wo(OkrCenterWorkInfo.class, Wo.class,
				null, Wo.Excludes);

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

	public static class WoOkrWorkBaseInfo extends OkrWorkBaseInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();
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

	public static class WoOkrWorkAuthorizeRecord extends OkrWorkAuthorizeRecord {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

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
}
