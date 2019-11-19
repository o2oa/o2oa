package com.x.okr.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 中心工作信息管理实体类
 * 
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrCenterWorkInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.OkrCenterWorkInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrCenterWorkInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrCenterWorkInfo.table;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	public void onPersist() throws Exception {
	}
	/*
	 * =============================================================================
	 * ===== 以上为 JpaObject 默认字段
	 * =============================================================================
	 * =====
	 */

	/*
	 * =============================================================================
	 * ===== 以下为具体不同的业务及数据表字段要求
	 * =============================================================================
	 * =====
	 */
	public static final String title_FIELDNAME = "title";
	@FieldDescribe("中心标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + title_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String title = "";

	public static final String deployYear_FIELDNAME = "deployYear";
	@FieldDescribe("工作部署年份")
	@Column(length = JpaObject.length_8B, name = ColumnNamePrefix + deployYear_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + deployYear_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String deployYear = "";

	public static final String deployMonth_FIELDNAME = "deployMonth";
	@FieldDescribe("工作部署月份")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + deployMonth_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + deployMonth_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String deployMonth = "";

	public static final String deployerName_FIELDNAME = "deployerName";
	@FieldDescribe("部署者姓名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ deployerName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String deployerName = "";

	public static final String deployerIdentity_FIELDNAME = "deployerIdentity";
	@FieldDescribe("部署者身份")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ deployerIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String deployerIdentity = "";

	public static final String deployerUnitName_FIELDNAME = "deployerUnitName";
	@FieldDescribe("部署者所属组织名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ deployerUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String deployerUnitName = "";

	public static final String deployerTopUnitName_FIELDNAME = "deployerTopUnitName";
	@FieldDescribe("部署者所属顶层组织名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ deployerTopUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String deployerTopUnitName = "";

	public static final String creatorName_FIELDNAME = "creatorName";
	@FieldDescribe("创建者姓名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorName = "";

	public static final String creatorIdentity_FIELDNAME = "creatorIdentity";
	@FieldDescribe("创建者身份")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity = "";

	public static final String creatorUnitName_FIELDNAME = "creatorUnitName";
	@FieldDescribe("创建者所属组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorUnitName = "";

	public static final String creatorTopUnitName_FIELDNAME = "creatorTopUnitName";
	@FieldDescribe("创建者所属顶层组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorTopUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorTopUnitName = "";

	public static final String processStatus_FIELDNAME = "processStatus";
	@FieldDescribe("中心工作处理状态：草稿|待审核|待确认|执行中|已完成|已撤消")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + processStatus_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processStatus = "草稿";

	public static final String deployDateStr_FIELDNAME = "deployDateStr";
	@FieldDescribe("中心工作部署日期-字符串，显示用：yyyy-mm-dd")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + deployDateStr_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String deployDateStr = "";

	public static final String defaultCompleteDateLimit_FIELDNAME = "defaultCompleteDateLimit";
	@FieldDescribe("中心工作默认完成日期")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + defaultCompleteDateLimit_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date defaultCompleteDateLimit = null;

	public static final String defaultCompleteDateLimitStr_FIELDNAME = "defaultCompleteDateLimitStr";
	@FieldDescribe("中心工作默认完成日期-字符串，显示用：yyyy-mm-dd")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + defaultCompleteDateLimitStr_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String defaultCompleteDateLimitStr = "";

	public static final String defaultWorkType_FIELDNAME = "defaultWorkType";
	@FieldDescribe("中心工作默认工作类别")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + defaultWorkType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String defaultWorkType = "";

	public static final String defaultWorkLevel_FIELDNAME = "defaultWorkLevel";
	@FieldDescribe("中心工作默认工作级别")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + defaultWorkLevel_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String defaultWorkLevel = "";

	public static final String isNeedAudit_FIELDNAME = "isNeedAudit";
	@FieldDescribe("中心工作是否需要审核")
	@Column(name = ColumnNamePrefix + isNeedAudit_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean isNeedAudit = false;

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("处理状态：正常|已删除")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + status_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String status = "正常";

	public static final String description_FIELDNAME = "description";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("中心工作描述")
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description = "";

	public static final String workTotal_FIELDNAME = "workTotal";
	@FieldDescribe("工作总个数")
	@Column(name = ColumnNamePrefix + workTotal_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long workTotal = 0L;

	public static final String processingWorkCount_FIELDNAME = "processingWorkCount";
	@FieldDescribe("执行中工作个数")
	@Column(name = ColumnNamePrefix + processingWorkCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long processingWorkCount = 0L;

	public static final String completedWorkCount_FIELDNAME = "completedWorkCount";
	@FieldDescribe("已完成工作个数")
	@Column(name = ColumnNamePrefix + completedWorkCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long completedWorkCount = 0L;

	public static final String overtimeWorkCount_FIELDNAME = "overtimeWorkCount";
	@FieldDescribe("已超时工作个数")
	@Column(name = ColumnNamePrefix + overtimeWorkCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long overtimeWorkCount = 0L;

	public static final String draftWorkCount_FIELDNAME = "draftWorkCount";
	@FieldDescribe("草稿工作个数")
	@Column(name = ColumnNamePrefix + draftWorkCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long draftWorkCount = 0L;

	public static final String defaultLeaderList_FIELDNAME = "defaultLeaderList";
	@FieldDescribe("中心工作默认阅知领导姓名")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ defaultLeaderList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ defaultLeaderList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ defaultLeaderList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + defaultLeaderList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> defaultLeaderList;

	public static final String defaultLeaderIdentityList_FIELDNAME = "defaultLeaderIdentityList";
	@FieldDescribe("中心工作默认阅知领导身份")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ defaultLeaderIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ defaultLeaderIdentityList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ defaultLeaderIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + defaultLeaderIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> defaultLeaderIdentityList;

	public static final String reportAuditLeaderNameList_FIELDNAME = "reportAuditLeaderNameList";
	@FieldDescribe("工作汇报审批领导(可多值，显示用)")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ reportAuditLeaderNameList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ reportAuditLeaderNameList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ reportAuditLeaderNameList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + reportAuditLeaderNameList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> reportAuditLeaderNameList;

	public static final String reportAuditLeaderIdentityList_FIELDNAME = "reportAuditLeaderIdentityList";
	@FieldDescribe("工作汇报审批领导身份(可多值，计算组织和顶层组织用)")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ reportAuditLeaderIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ reportAuditLeaderIdentityList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ reportAuditLeaderIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + reportAuditLeaderIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)

	private List<String> reportAuditLeaderIdentityList;

	public static final String reportAuditLeaderUnitNameList_FIELDNAME = "reportAuditLeaderUnitNameList";
	@FieldDescribe("审核者所属组织，多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ reportAuditLeaderUnitNameList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ reportAuditLeaderUnitNameList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ reportAuditLeaderUnitNameList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + reportAuditLeaderUnitNameList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> reportAuditLeaderUnitNameList;

	public static final String reportAuditLeaderTopUnitNameList_FIELDNAME = "reportAuditLeaderTopUnitNameList";
	@FieldDescribe("审核者所属顶层组织，多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ reportAuditLeaderTopUnitNameList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ reportAuditLeaderTopUnitNameList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ reportAuditLeaderTopUnitNameList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + reportAuditLeaderTopUnitNameList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> reportAuditLeaderTopUnitNameList;

	public static final String attachmentList_FIELDNAME = "attachmentList";
	@FieldDescribe("附件列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + attachmentList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + attachmentList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ attachmentList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + attachmentList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> attachmentList;

	/**
	 * 获取中心工作标题
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 设置中心工作标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 获取部署者姓名
	 * 
	 * @return
	 */
	public String getDeployerName() {
		return deployerName;
	}

	/**
	 * 设置部署者姓名
	 * 
	 * @param deployerName
	 */
	public void setDeployerName(String deployerName) {
		this.deployerName = deployerName;
	}

	/**
	 * 获取部署者所属组织名称
	 * 
	 * @return
	 */
	public String getDeployerUnitName() {
		return deployerUnitName;
	}

	/**
	 * 设置部署者所属组织名称
	 * 
	 * @param deployerUnitName
	 */
	public void setDeployerUnitName(String deployerUnitName) {
		this.deployerUnitName = deployerUnitName;
	}

	/**
	 * 获取部署者所属顶层组织名称
	 * 
	 * @return
	 */
	public String getDeployerTopUnitName() {
		return deployerTopUnitName;
	}

	/**
	 * 设置部署者所属顶层组织名称
	 * 
	 * @param deployerTopUnitName
	 */
	public void setDeployerTopUnitName(String deployerTopUnitName) {
		this.deployerTopUnitName = deployerTopUnitName;
	}

	/**
	 * 获取创建者姓名
	 * 
	 * @return
	 */
	public String getCreatorName() {
		return creatorName;
	}

	/**
	 * 设置创建者姓名
	 * 
	 * @param creatorName
	 */
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	/**
	 * 获取创建者所属组织名称
	 * 
	 * @return
	 */
	public String getCreatorUnitName() {
		return creatorUnitName;
	}

	/**
	 * 设置创建者所属组织名称
	 * 
	 * @param creatorUnitName
	 */
	public void setCreatorUnitName(String creatorUnitName) {
		this.creatorUnitName = creatorUnitName;
	}

	/**
	 * 获取创建者所属顶层组织名称
	 * 
	 * @return
	 */
	public String getCreatorTopUnitName() {
		return creatorTopUnitName;
	}

	/**
	 * 设置创建者所属顶层组织名称
	 * 
	 * @param creatorTopUnitName
	 */
	public void setCreatorTopUnitName(String creatorTopUnitName) {
		this.creatorTopUnitName = creatorTopUnitName;
	}

	/**
	 * 获取中心工作信息处理状态：草稿|待确认|执行中|已完成|已撤消
	 * 
	 * @return
	 */
	public String getProcessStatus() {
		return processStatus;
	}

	/**
	 * 设置中心工作信息处理状态：草稿|待确认|执行中|已完成|已撤消
	 * 
	 * @param processStatus
	 */
	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	/**
	 * 获取中心工作下属工作默认完成日期
	 * 
	 * @return
	 */
	public Date getDefaultCompleteDateLimit() {
		return defaultCompleteDateLimit;
	}

	/**
	 * 设置中心工作下属工作默认完成日期
	 * 
	 * @param defaultCompleteDateLimit
	 */
	public void setDefaultCompleteDateLimit(Date defaultCompleteDateLimit) {
		this.defaultCompleteDateLimit = defaultCompleteDateLimit;
	}

	/**
	 * 获取中心工作下属工作默认完成日期（字符串）
	 * 
	 * @return
	 */
	public String getDefaultCompleteDateLimitStr() {
		return defaultCompleteDateLimitStr;
	}

	/**
	 * 设置中心工作下属工作默认完成日期（字符串）
	 * 
	 * @param defaultCompleteDateLimitStr
	 */
	public void setDefaultCompleteDateLimitStr(String defaultCompleteDateLimitStr) {
		this.defaultCompleteDateLimitStr = defaultCompleteDateLimitStr;
	}

	/**
	 * 获取中心工作下属工作默认工作类别
	 * 
	 * @return
	 */
	public String getDefaultWorkType() {
		return defaultWorkType;
	}

	/**
	 * 设置中心工作下属工作默认工作类别
	 * 
	 * @param defaultWorkType
	 */
	public void setDefaultWorkType(String defaultWorkType) {
		this.defaultWorkType = defaultWorkType;
	}

	/**
	 * 获取中心工作下属工作默认工作级别
	 * 
	 * @return
	 */
	public String getDefaultWorkLevel() {
		return defaultWorkLevel;
	}

	/**
	 * 设置中心工作下属工作默认工作级别
	 * 
	 * @param defaultWorkLevel
	 */
	public void setDefaultWorkLevel(String defaultWorkLevel) {
		this.defaultWorkLevel = defaultWorkLevel;
	}

	/**
	 * 获取工作部署年份
	 * 
	 * @return
	 */
	public String getDeployYear() {
		return deployYear;
	}

	/**
	 * 设置工作部署年份
	 * 
	 * @param deployYear
	 */
	public void setDeployYear(String deployYear) {
		this.deployYear = deployYear;
	}

	/**
	 * 获取工作部署月份
	 * 
	 * @return
	 */
	public String getDeployMonth() {
		return deployMonth;
	}

	/**
	 * 设置工作部署月份
	 * 
	 * @param deployMonth
	 */
	public void setDeployMonth(String deployMonth) {
		this.deployMonth = deployMonth;
	}

	/**
	 * 获取是否需要审核
	 * 
	 * @return
	 */
	public Boolean getIsNeedAudit() {
		return isNeedAudit;
	}

	/**
	 * 设置是否需要审核
	 * 
	 * @param isNeedAudit
	 */
	public void setIsNeedAudit(Boolean isNeedAudit) {
		this.isNeedAudit = isNeedAudit;
	}

	/**
	 * 获取信息状态：正常|已删除
	 * 
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 设置信息状态：正常|已删除
	 * 
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 获取中心工作描述
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置中心工作描述
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
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

	public Long getWorkTotal() {
		return workTotal;
	}

	public void setWorkTotal(Long workTotal) {
		this.workTotal = workTotal;
	}

	public Long getProcessingWorkCount() {
		return processingWorkCount;
	}

	public void setProcessingWorkCount(Long processingWorkCount) {
		this.processingWorkCount = processingWorkCount;
	}

	public Long getCompletedWorkCount() {
		return completedWorkCount;
	}

	public void setCompletedWorkCount(Long completedWorkCount) {
		this.completedWorkCount = completedWorkCount;
	}

	public Long getOvertimeWorkCount() {
		return overtimeWorkCount;
	}

	public void setOvertimeWorkCount(Long overtimeWorkCount) {
		this.overtimeWorkCount = overtimeWorkCount;
	}

	public Long getDraftWorkCount() {
		return draftWorkCount;
	}

	public void setDraftWorkCount(Long draftWorkCount) {
		this.draftWorkCount = draftWorkCount;
	}

	public List<String> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(List<String> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public String getDeployDateStr() {
		return deployDateStr;
	}

	public void setDeployDateStr(String deployDateStr) {
		this.deployDateStr = deployDateStr;
	}

	public List<String> getDefaultLeaderList() {
		return defaultLeaderList == null ? new ArrayList<>() : defaultLeaderList;
	}

	public List<String> getDefaultLeaderIdentityList() {
		return defaultLeaderIdentityList == null ? new ArrayList<>() : defaultLeaderIdentityList;
	}

	public List<String> getReportAuditLeaderNameList() {
		return reportAuditLeaderNameList == null ? new ArrayList<>() : reportAuditLeaderNameList;
	}

	public List<String> getReportAuditLeaderIdentityList() {
		return reportAuditLeaderIdentityList == null ? new ArrayList<>() : reportAuditLeaderIdentityList;
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

	public List<String> getReportAuditLeaderUnitNameList() {
		return reportAuditLeaderUnitNameList == null ? new ArrayList<>() : reportAuditLeaderUnitNameList;
	}

	public List<String> getReportAuditLeaderTopUnitNameList() {
		return reportAuditLeaderTopUnitNameList == null ? new ArrayList<>() : reportAuditLeaderTopUnitNameList;
	}

	public void setReportAuditLeaderUnitNameList(List<String> reportAuditLeaderUnitNameList) {
		this.reportAuditLeaderUnitNameList = reportAuditLeaderUnitNameList;
	}

	public void setReportAuditLeaderTopUnitNameList(List<String> reportAuditLeaderTopUnitNameList) {
		this.reportAuditLeaderTopUnitNameList = reportAuditLeaderTopUnitNameList;
	}

}