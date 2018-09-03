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
	@FieldDescribe("中心标题")
	@Column(name = "xtitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = false)
	private String title = "";

	@FieldDescribe("工作部署年份")
	@Column(name = "xdeployYear", length = JpaObject.length_16B)
	@Index(name = TABLE + "_deployYear")
	@CheckPersist(allowEmpty = true)
	private String deployYear = "";

	@FieldDescribe("工作部署月份")
	@Column(name = "xdeployMonth", length = JpaObject.length_16B)
	@Index(name = TABLE + "_deployMonth")
	@CheckPersist(allowEmpty = true)
	private String deployMonth = "";

	@FieldDescribe("部署者姓名")
	@Column(name = "xdeployerName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String deployerName = "";

	@FieldDescribe("部署者身份")
	@Column(name = "xdeployerIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String deployerIdentity = "";

	@FieldDescribe("部署者所属组织名称")
	@Column(name = "xdeployerUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String deployerUnitName = "";

	@FieldDescribe("部署者所属顶层组织名称")
	@Column(name = "xdeployerTopUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String deployerTopUnitName = "";

	@FieldDescribe("创建者姓名")
	@Column(name = "xcreatorName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String creatorName = "";

	@FieldDescribe("创建者身份")
	@Column(name = "xcreatorIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity = "";

	@FieldDescribe("创建者所属组织")
	@Column(name = "xcreatorUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String creatorUnitName = "";

	@FieldDescribe("创建者所属顶层组织")
	@Column(name = "xcreatorTopUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String creatorTopUnitName = "";

	@FieldDescribe("中心工作处理状态：草稿|待审核|待确认|执行中|已完成|已撤消")
	@Column(name = "xprocessStatus", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String processStatus = "草稿";

	@FieldDescribe("中心工作部署日期-字符串，显示用：yyyy-mm-dd")
	@Column(name = "xdeployDateStr", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String deployDateStr = "";

	@FieldDescribe("中心工作默认完成日期")
	@Column(name = "xdefaultCompleteDateLimit")
	@CheckPersist(allowEmpty = true)
	private Date defaultCompleteDateLimit = null;

	@FieldDescribe("中心工作默认完成日期-字符串，显示用：yyyy-mm-dd")
	@Column(name = "xdefaultCompleteDateLimitStr", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String defaultCompleteDateLimitStr = "";

	@FieldDescribe("中心工作默认工作类别")
	@Column(name = "xdefaultWorkType", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String defaultWorkType = "";

	@FieldDescribe("中心工作默认工作级别")
	@Column(name = "xdefaultWorkLevel", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String defaultWorkLevel = "";

	@FieldDescribe("中心工作默认阅知领导姓名")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_defaultLeaderList", joinIndex = @Index(name = TABLE + "_defaultLeaderList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xdefaultLeaderList")
	@ElementIndex(name = TABLE + "_defaultLeaderList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> defaultLeaderList;

	@FieldDescribe("中心工作默认阅知领导身份")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE
			+ "_defaultLeaderIdentityList", joinIndex = @Index(name = TABLE + "_defaultLeaderIdentityList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xdefaultLeaderIdentityList")
	@ElementIndex(name = TABLE + "_defaultLeaderIdentityList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> defaultLeaderIdentityList;

	@FieldDescribe("工作汇报审批领导(可多值，显示用)")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE
			+ "_reportAuditLeaderNameList", joinIndex = @Index(name = TABLE + "_reportAuditLeaderNameList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xreportAuditLeaderNameList")
	@ElementIndex(name = TABLE + "_reportAuditLeaderNameList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> reportAuditLeaderNameList;

	@FieldDescribe("工作汇报审批领导身份(可多值，计算组织和顶层组织用)")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_reportAuditLeaderIdentityList", joinIndex = @Index(name = TABLE
			+ "_reportAuditLeaderIdentityList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xreportAuditLeaderIdentityList")
	@ElementIndex(name = TABLE + "_reportAuditLeaderIdentityList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> reportAuditLeaderIdentityList;

	// @FieldDescribe("审核者姓名，多值")
	// @PersistentCollection(fetch = FetchType.EAGER)
	// @OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	// @ContainerTable(name = TABLE + "_auditLeaderNameList", joinIndex =
	// @Index(name = TABLE + "_auditLeaderNameList_join"))
	// @ElementColumn(length =
	// AbstractPersistenceProperties.organization_name_length, name =
	// "xauditLeaderNameList")
	// @ElementIndex(name = TABLE + "_auditLeaderNameList_element")
	// @CheckPersist(allowEmpty = true)
	// private List<String> auditLeaderNameList;
	//
	// @FieldDescribe("审核者身份，多值")
	// @PersistentCollection(fetch = FetchType.EAGER)
	// @OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	// @ContainerTable(name = TABLE + "_auditLeaderIdentityList", joinIndex =
	// @Index(name = TABLE + "_auditLeaderIdentityList_join"))
	// @ElementColumn(length =
	// AbstractPersistenceProperties.organization_name_length, name =
	// "xauditLeaderIdentityList")
	// @ElementIndex(name = TABLE + "_auditLeaderIdentityList_element")
	// @CheckPersist(allowEmpty = true)
	// private List<String> auditLeaderIdentityList;

	@FieldDescribe("审核者所属组织，多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_reportAuditLeaderUnitNameList", joinIndex = @Index(name = TABLE
			+ "_reportAuditLeaderUnitNameList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xreportAuditLeaderUnitNameList")
	@ElementIndex(name = TABLE + "_reportAuditLeaderUnitNameList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> reportAuditLeaderUnitNameList;

	@FieldDescribe("审核者所属顶层组织，多值")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_reportAuditLeaderTopUnitNameList", joinIndex = @Index(name = TABLE
			+ "_reportAuditLeaderTopUnitNameList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xreportAuditLeaderTopUnitNameList")
	@ElementIndex(name = TABLE + "_reportAuditLeaderTopUnitNameList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> reportAuditLeaderTopUnitNameList;

	@FieldDescribe("中心工作是否需要审核")
	@Column(name = "xisNeedAudit")
	@CheckPersist(allowEmpty = true)
	private Boolean isNeedAudit = false;

	@FieldDescribe("处理状态：正常|已删除")
	@Column(name = "xstatus", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = false)
	private String status = "正常";

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("中心工作描述")
	@Column(name = "xdescription", length = JpaObject.length_2K)
	@CheckPersist(allowEmpty = true)
	private String description = "";

	@FieldDescribe("工作总个数")
	@Column(name = "xworkTotal")
	@CheckPersist(allowEmpty = true)
	private Long workTotal = 0L;

	@FieldDescribe("执行中工作个数")
	@Column(name = "xprocessingWorkCount")
	@CheckPersist(allowEmpty = true)
	private Long processingWorkCount = 0L;

	@FieldDescribe("已完成工作个数")
	@Column(name = "xcompletedWorkCount")
	@CheckPersist(allowEmpty = true)
	private Long completedWorkCount = 0L;

	@FieldDescribe("已超时工作个数")
	@Column(name = "xovertimeWorkCount")
	@CheckPersist(allowEmpty = true)
	private Long overtimeWorkCount = 0L;

	@FieldDescribe("草稿工作个数")
	@Column(name = "xdraftWorkCount")
	@CheckPersist(allowEmpty = true)
	private Long draftWorkCount = 0L;

	@FieldDescribe("附件列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_attachmentList", joinIndex = @Index(name = TABLE + "_attachmentList_join"))
	@ElementColumn(length = JpaObject.length_id)
	@ElementIndex(name = TABLE + "_attachmentList_element")
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