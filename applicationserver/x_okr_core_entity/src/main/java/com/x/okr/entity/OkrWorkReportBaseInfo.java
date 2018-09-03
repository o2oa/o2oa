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
	@FieldDescribe("汇报提交时间.")
	@Column(name = "xsubmitTime")
	private Date submitTime;

	@FieldDescribe("工作标题")
	@Column(name = "xworkTitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = false)
	private String workTitle = null;

	@FieldDescribe("中心工作标题")
	@Column(name = "xcenterTitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = false)
	private String centerTitle = null;

	@FieldDescribe("所属中心工作ID")
	@Column(name = "xcenterId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = false)
	private String centerId = null;

	@FieldDescribe("所属工作ID")
	@Column(name = "xworkId", length = JpaObject.length_id)
	@Index(name = TABLE + "_workId")
	@CheckPersist(allowEmpty = false)
	private String workId = null;

	@FieldDescribe("工作类别")
	@Column(name = "xworkType", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String workType = "";

	@FieldDescribe("工作汇报标题")
	@Column(name = "xtitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = false)
	private String title = null;

	@FieldDescribe("工作汇报短标题")
	@Column(name = "xshortTitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String shortTitle = null;

	@FieldDescribe("工作汇报当前环节")
	@Column(name = "xactivityName", length = JpaObject.length_64B)
	@CheckPersist(allowEmpty = true)
	private String activityName = "草稿";

	@FieldDescribe("工作汇报次序")
	@Column(name = "xreportCount")
	@CheckPersist(allowEmpty = true)
	private Integer reportCount = 0;

	@FieldDescribe("汇报者姓名")
	@Column(name = "xreporterName", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_reporterName")
	@CheckPersist(allowEmpty = true)
	private String reporterName = null;

	@FieldDescribe("汇报者身份")
	@Column(name = "xreporterIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_reporterIdentity")
	@CheckPersist(allowEmpty = true)
	private String reporterIdentity = null;

	@FieldDescribe("汇报者所属组织")
	@Column(name = "xreporterUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String reporterUnitName = null;

	@FieldDescribe("汇报者所属顶层组织")
	@Column(name = "xreporterTopUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String reporterTopUnitName = null;

	@FieldDescribe("创建者姓名")
	@Column(name = "xcreatorName", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_creatorName")
	@CheckPersist(allowEmpty = true)
	private String creatorName = null;

	@FieldDescribe("创建者身份")
	@Column(name = "xcreatorIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_creatorIdentity")
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity = null;

	@FieldDescribe("创建者所属组织")
	@Column(name = "xcreatorUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String creatorUnitName = null;

	@FieldDescribe("创建者所属顶层组织")
	@Column(name = "xcreatorTopUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String creatorTopUnitName = null;

	@FieldDescribe("工作是否已经完成")
	@Column(name = "xisWorkCompleted")
	private Boolean isWorkCompleted = false;

	@FieldDescribe("工作完成进度 % ")
	@Column(name = "xprogressPercent")
	private Integer progressPercent = 0;

	@FieldDescribe("汇报处理状态：草稿|管理员督办|领导批示|已完成")
	@Column(name = "xprocessStatus", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String processStatus = "草稿";

	@FieldDescribe("汇报信息状态：正常|已删除")
	@Column(name = "xstatus", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String status = "正常";

	@FieldDescribe("汇报处理类别：审批|阅知")
	@Column(name = "xprocessType", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String processType = "";

	@FieldDescribe("当前处理级别")
	@Column(name = "xcurrentProcessLevel")
	@Index(name = TABLE + "_currentProcessLevel")
	@CheckPersist(allowEmpty = true)
	private Integer currentProcessLevel = 0;

	@FieldDescribe("当前处理人姓名，多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE
			+ "_currentProcessorNameList", joinIndex = @Index(name = TABLE + "_currentProcessorNameList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xcurrentProcessorNameList")
	@ElementIndex(name = TABLE + "_currentProcessorNameList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> currentProcessorNameList;

	@FieldDescribe("当前处理人身份，多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE
			+ "_currentProcessorIdentityList", joinIndex = @Index(name = TABLE + "_currentProcessorIdentityList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xcurrentProcessorIdentityList")
	@ElementIndex(name = TABLE + "_currentProcessorIdentityList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> currentProcessorIdentityList;

	@FieldDescribe("当前处理人所属组织，多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE
			+ "_currentProcessorUnitNameList", joinIndex = @Index(name = TABLE + "_currentProcessorUnitNameList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xcurrentProcessorUnitNameList")
	@ElementIndex(name = TABLE + "_currentProcessorUnitNameList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> currentProcessorUnitNameList;

	@FieldDescribe("当前处理人所属顶层组织，多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_currentProcessorTopUnitNameList", joinIndex = @Index(name = TABLE
			+ "_currentProcessorTopUnitNameList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xcurrentProcessorTopUnitNameList")
	@ElementIndex(name = TABLE + "_currentProcessorTopUnitNameList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> currentProcessorTopUnitNameList;

	@FieldDescribe("是否需要工作管理员审核")
	@Column(name = "xneedAdminAudit")
	@CheckPersist(allowEmpty = true)
	private Boolean needAdminAudit = false;

	@FieldDescribe("是否需要领导批示")
	@Column(name = "xneedLeaderRead")
	@CheckPersist(allowEmpty = true)
	private Boolean needLeaderRead = false;

	@FieldDescribe("工作管理员姓名")
	@Column(name = "xworkAdminName", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String workAdminName = null;

	@FieldDescribe("工作管理员身份")
	@Column(name = "xworkAdminIdentity", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String workAdminIdentity = null;

	@FieldDescribe("批示领导姓名列表, 多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE
			+ "_readLeadersNameList", joinIndex = @Index(name = TABLE + "_readLeadersNameList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xreadLeadersNameList")
	@ElementIndex(name = TABLE + "_readLeadersNameList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> readLeadersNameList;

	@FieldDescribe("批示领导身份列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE
			+ "_readLeadersIdentityList", joinIndex = @Index(name = TABLE + "_readLeadersIdentityList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xreadLeadersIdentityList")
	@ElementIndex(name = TABLE + "_readLeadersIdentityList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> readLeadersIdentityList;

	@FieldDescribe("汇报工作流处理方式")
	@Column(name = "xreportWorkflowType", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String reportWorkflowType = null;

	@FieldDescribe("说明备注")
	@Column(name = "xdescription", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String description = null;

	@FieldDescribe("附件列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_attachmentList", joinIndex = @Index(name = TABLE + "_attachmentList_join"))
	@ElementColumn(length = JpaObject.length_id)
	@ElementIndex(name = TABLE + "_attachmentList_element")
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