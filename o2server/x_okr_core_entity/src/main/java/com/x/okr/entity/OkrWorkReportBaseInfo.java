package com.x.okr.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
 * 工作汇报基础信息管理实体类
 * 
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrWorkReportBaseInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.OkrWorkReportBaseInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrWorkReportBaseInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrWorkReportBaseInfo.table;

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
	public static final String submitTime_FIELDNAME = "submitTime";
	@FieldDescribe("汇报提交时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + submitTime_FIELDNAME)
	private Date submitTime;

	public static final String workTitle_FIELDNAME = "workTitle";
	@FieldDescribe("工作标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + workTitle_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String workTitle = null;

	public static final String centerTitle_FIELDNAME = "centerTitle";
	@FieldDescribe("中心工作标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + centerTitle_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String centerTitle = null;

	public static final String centerId_FIELDNAME = "centerId";
	@FieldDescribe("所属中心工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + centerId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String centerId = null;

	public static final String workId_FIELDNAME = "workId";
	@FieldDescribe("所属工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + workId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String workId = null;

	public static final String workType_FIELDNAME = "workType";
	@FieldDescribe("工作类别")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + workType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workType = "";

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("工作汇报标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String title = null;

	public static final String shortTitle_FIELDNAME = "shortTitle";
	@FieldDescribe("工作汇报短标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + shortTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String shortTitle = null;

	public static final String activityName_FIELDNAME = "activityName";
	@FieldDescribe("工作汇报当前环节")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + activityName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityName = "草稿";

	public static final String reportCount_FIELDNAME = "reportCount";
	@FieldDescribe("工作汇报次序")
	@Column(name = ColumnNamePrefix + reportCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer reportCount = 0;

	public static final String reporterName_FIELDNAME = "reporterName";
	@FieldDescribe("汇报者姓名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ reporterName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + reporterName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String reporterName = null;

	public static final String reporterIdentity_FIELDNAME = "reporterIdentity";
	@FieldDescribe("汇报者身份")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ reporterIdentity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + reporterIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String reporterIdentity = null;

	public static final String reporterUnitName_FIELDNAME = "reporterUnitName";
	@FieldDescribe("汇报者所属组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ reporterUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String reporterUnitName = null;

	public static final String reporterTopUnitName_FIELDNAME = "reporterTopUnitName";
	@FieldDescribe("汇报者所属顶层组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ reporterTopUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String reporterTopUnitName = null;

	public static final String creatorName_FIELDNAME = "creatorName";
	@FieldDescribe("创建者姓名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorName = null;

	public static final String creatorIdentity_FIELDNAME = "creatorIdentity";
	@FieldDescribe("创建者身份")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorIdentity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity = null;

	public static final String creatorUnitName_FIELDNAME = "creatorUnitName";
	@FieldDescribe("创建者所属组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorUnitName = null;

	public static final String creatorTopUnitName_FIELDNAME = "creatorTopUnitName";
	@FieldDescribe("创建者所属顶层组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorTopUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorTopUnitName = null;

	public static final String isWorkCompleted_FIELDNAME = "isWorkCompleted";
	@FieldDescribe("工作是否已经完成")
	@Column(name = ColumnNamePrefix + isWorkCompleted_FIELDNAME)
	private Boolean isWorkCompleted = false;

	public static final String progressPercent_FIELDNAME = "progressPercent";
	@FieldDescribe("工作完成进度 % ")
	@Column(name = ColumnNamePrefix + progressPercent_FIELDNAME)
	private Integer progressPercent = 0;

	public static final String processStatus_FIELDNAME = "processStatus";
	@FieldDescribe("汇报处理状态：草稿|管理员督办|领导批示|已完成")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + processStatus_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processStatus = "草稿";

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("汇报信息状态：正常|已删除")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + status_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String status = "正常";

	public static final String processType_FIELDNAME = "processType";
	@FieldDescribe("汇报处理类别：审批|阅知")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + processType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processType = "";

	public static final String currentProcessLevel_FIELDNAME = "currentProcessLevel";
	@FieldDescribe("当前处理级别")
	@Column(name = ColumnNamePrefix + currentProcessLevel_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + currentProcessLevel_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer currentProcessLevel = 0;

	public static final String needAdminAudit_FIELDNAME = "needAdminAudit";
	@FieldDescribe("是否需要工作管理员审核")
	@Column(name = ColumnNamePrefix + needAdminAudit_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean needAdminAudit = false;

	public static final String needLeaderRead_FIELDNAME = "needLeaderRead";
	@FieldDescribe("是否需要领导批示")
	@Column(name = ColumnNamePrefix + needLeaderRead_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean needLeaderRead = false;

	public static final String workAdminName_FIELDNAME = "workAdminName";
	@FieldDescribe("工作管理员姓名")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + workAdminName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workAdminName = null;

	public static final String workAdminIdentity_FIELDNAME = "workAdminIdentity";
	@FieldDescribe("工作管理员身份")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + workAdminIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workAdminIdentity = null;

	public static final String reportWorkflowType_FIELDNAME = "reportWorkflowType";
	@FieldDescribe("汇报工作流处理方式")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + reportWorkflowType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String reportWorkflowType = null;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("说明备注")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description = null;

	public static final String currentProcessorNameList_FIELDNAME = "currentProcessorNameList";
	@FieldDescribe("当前处理人姓名，多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ currentProcessorNameList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ currentProcessorNameList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ currentProcessorNameList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + currentProcessorNameList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> currentProcessorNameList;

	public static final String currentProcessorIdentityList_FIELDNAME = "currentProcessorIdentityList";
	@FieldDescribe("当前处理人身份，多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ currentProcessorIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ currentProcessorIdentityList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ currentProcessorIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + currentProcessorIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> currentProcessorIdentityList;

	public static final String currentProcessorUnitNameList_FIELDNAME = "currentProcessorUnitNameList";
	@FieldDescribe("当前处理人所属组织，多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ currentProcessorUnitNameList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ currentProcessorUnitNameList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ currentProcessorUnitNameList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + currentProcessorUnitNameList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> currentProcessorUnitNameList;

	public static final String currentProcessorTopUnitNameList_FIELDNAME = "currentProcessorTopUnitNameList";
	@FieldDescribe("当前处理人所属顶层组织，多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ currentProcessorTopUnitNameList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ currentProcessorTopUnitNameList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ currentProcessorTopUnitNameList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + currentProcessorTopUnitNameList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> currentProcessorTopUnitNameList;

	public static final String readLeadersNameList_FIELDNAME = "readLeadersNameList";
	@FieldDescribe("批示领导姓名列表, 多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ readLeadersNameList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ readLeadersNameList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ readLeadersNameList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readLeadersNameList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readLeadersNameList;

	public static final String readLeadersIdentityList_FIELDNAME = "readLeadersIdentityList";
	@FieldDescribe("批示领导身份列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ readLeadersIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ readLeadersIdentityList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ readLeadersIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readLeadersIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readLeadersIdentityList;

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
	 * 获取所属工作标题
	 * 
	 * @return
	 */
	public String getWorkTitle() {
		return workTitle;
	}

	/**
	 * 设置所属工作标题
	 * 
	 * @param workTitle
	 */
	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	/**
	 * 获取所属中心工作ID
	 * 
	 * @return
	 */
	public String getCenterId() {
		return centerId;
	}

	/**
	 * 设置所属中心工作ID
	 * 
	 * @param centerId
	 */
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	/**
	 * 获取所属工作ID
	 * 
	 * @return
	 */
	public String getWorkId() {
		return workId;
	}

	/**
	 * 设置所属工作ID
	 * 
	 * @param workId
	 */
	public void setWorkId(String workId) {
		this.workId = workId;
	}

	/**
	 * 获取汇报标题
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 设置汇报标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 获取汇报短标题
	 * 
	 * @return
	 */
	public String getShortTitle() {
		return shortTitle;
	}

	/**
	 * 设置汇报短标题
	 * 
	 * @param shortTitle
	 */
	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	/**
	 * 获取汇报次序
	 * 
	 * @return
	 */
	public Integer getReportCount() {
		return reportCount;
	}

	/**
	 * 设置汇报次序
	 * 
	 * @param reportCount
	 */
	public void setReportCount(Integer reportCount) {
		this.reportCount = reportCount;
	}

	/**
	 * 获取汇报者姓名
	 * 
	 * @return
	 */
	public String getReporterName() {
		return reporterName;
	}

	/**
	 * 设置汇报者姓名
	 * 
	 * @param reporterName
	 */
	public void setReporterName(String reporterName) {
		this.reporterName = reporterName;
	}

	/**
	 * 获取汇报者所属组织名称
	 * 
	 * @return
	 */
	public String getReporterUnitName() {
		return reporterUnitName;
	}

	/**
	 * 设置汇报者所属组织名称
	 * 
	 * @param reporterUnitName
	 */
	public void setReporterUnitName(String reporterUnitName) {
		this.reporterUnitName = reporterUnitName;
	}

	/**
	 * 获取汇报者所属顶层组织名称
	 * 
	 * @return
	 */
	public String getReporterTopUnitName() {
		return reporterTopUnitName;
	}

	/**
	 * 设置汇报者所属顶层组织名称
	 * 
	 * @param reporterTopUnitName
	 */
	public void setReporterTopUnitName(String reporterTopUnitName) {
		this.reporterTopUnitName = reporterTopUnitName;
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
	 * 获取工作是否已经完成
	 * 
	 * @return
	 */
	public Boolean getIsWorkCompleted() {
		return isWorkCompleted;
	}

	/**
	 * 设置工作是否已经完成
	 * 
	 * @param isWorkCompleted
	 */
	public void setIsWorkCompleted(Boolean isWorkCompleted) {
		this.isWorkCompleted = isWorkCompleted;
	}

	public Integer getProgressPercent() {
		return progressPercent;
	}

	public void setProgressPercent(Integer progressPercent) {
		this.progressPercent = progressPercent;
	}

	/**
	 * 获取汇报信息状态：正常|已删除
	 * 
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 设置汇报信息状态：正常|已删除
	 * 
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 获取汇报处理类别：审批|阅知
	 * 
	 * @return
	 */
	public String getProcessType() {
		return processType;
	}

	/**
	 * 设置汇报处理类别：审批|阅知
	 * 
	 * @param processType
	 */
	public void setProcessType(String processType) {
		this.processType = processType;
	}

	/**
	 * 获取中心工作标题
	 * 
	 * @return
	 */
	public String getCenterTitle() {
		return centerTitle;
	}

	/**
	 * 设置中心工作标题
	 * 
	 * @param centerTitle
	 */
	public void setCenterTitle(String centerTitle) {
		this.centerTitle = centerTitle;
	}

	/**
	 * 获取汇报处理状态：草稿|审阅中|汇报人确认|已归档|已撤回
	 * 
	 * @return
	 */
	public String getProcessStatus() {
		return processStatus;
	}

	/**
	 * 设置汇报处理状态：草稿|审阅中|汇报人确认|已归档|已撤回
	 * 
	 * @param processStatus
	 */
	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	/**
	 * 获取是否需要工作管理员审核
	 * 
	 * @return
	 */
	public Boolean getNeedAdminAudit() {
		return needAdminAudit;
	}

	/**
	 * 设置是否需要工作管理员审核
	 * 
	 * @param needAdminAudit
	 */
	public void setNeedAdminAudit(Boolean needAdminAudit) {
		this.needAdminAudit = needAdminAudit;
	}

	/**
	 * 获取工作管理员姓名
	 * 
	 * @return
	 */
	public String getWorkAdminName() {
		return workAdminName;
	}

	/**
	 * 设置工作管理员姓名
	 * 
	 * @param workAdminName
	 */
	public void setWorkAdminName(String workAdminName) {
		this.workAdminName = workAdminName;
	}

	public String getReporterIdentity() {
		return reporterIdentity;
	}

	public void setReporterIdentity(String reporterIdentity) {
		this.reporterIdentity = reporterIdentity;
	}

	public String getCreatorIdentity() {
		return creatorIdentity;
	}

	public void setCreatorIdentity(String creatorIdentity) {
		this.creatorIdentity = creatorIdentity;
	}

	public String getWorkAdminIdentity() {
		return workAdminIdentity;
	}

	public void setWorkAdminIdentity(String workAdminIdentity) {
		this.workAdminIdentity = workAdminIdentity;
	}

	public Boolean getNeedLeaderRead() {
		return needLeaderRead;
	}

	public void setNeedLeaderRead(Boolean needLeaderRead) {
		this.needLeaderRead = needLeaderRead;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReportWorkflowType() {
		return reportWorkflowType;
	}

	public void setReportWorkflowType(String reportWorkflowType) {
		this.reportWorkflowType = reportWorkflowType;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public Integer getCurrentProcessLevel() {
		return currentProcessLevel;
	}

	public void setCurrentProcessLevel(Integer currentProcessLevel) {
		this.currentProcessLevel = currentProcessLevel;
	}

	public String getWorkType() {
		return workType;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}

	public Date getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}

	public List<String> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(List<String> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public List<String> getReadLeadersNameList() {
		return readLeadersNameList == null ? new ArrayList<>() : readLeadersNameList;
	}

	public List<String> getReadLeadersIdentityList() {
		return readLeadersIdentityList == null ? new ArrayList<>() : readLeadersIdentityList;
	}

	public void setReadLeadersNameList(List<String> readLeadersNameList) {
		this.readLeadersNameList = readLeadersNameList;
	}

	public void setReadLeadersIdentityList(List<String> readLeadersIdentityList) {
		this.readLeadersIdentityList = readLeadersIdentityList;
	}

	public List<String> getCurrentProcessorNameList() {
		return currentProcessorNameList == null ? new ArrayList<>() : currentProcessorNameList;
	}

	public List<String> getCurrentProcessorIdentityList() {
		return currentProcessorIdentityList == null ? new ArrayList<>() : currentProcessorIdentityList;
	}

	public List<String> getCurrentProcessorUnitNameList() {
		return currentProcessorUnitNameList == null ? new ArrayList<>() : currentProcessorUnitNameList;
	}

	public List<String> getCurrentProcessorTopUnitNameList() {
		return currentProcessorTopUnitNameList == null ? new ArrayList<>() : currentProcessorTopUnitNameList;
	}

	public void setCurrentProcessorNameList(List<String> currentProcessorNameList) {
		this.currentProcessorNameList = currentProcessorNameList;
	}

	public void setCurrentProcessorIdentityList(List<String> currentProcessorIdentityList) {
		this.currentProcessorIdentityList = currentProcessorIdentityList;
	}

	public void setCurrentProcessorUnitNameList(List<String> currentProcessorUnitNameList) {
		this.currentProcessorUnitNameList = currentProcessorUnitNameList;
	}

	public void setCurrentProcessorTopUnitNameList(List<String> currentProcessorTopUnitNameList) {
		this.currentProcessorTopUnitNameList = currentProcessorTopUnitNameList;
	}
}