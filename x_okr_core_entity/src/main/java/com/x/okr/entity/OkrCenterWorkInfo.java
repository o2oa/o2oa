package com.x.okr.entity;

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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
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
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

/**
 * 中心工作信息管理实体类
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrCenterWorkInfo.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrCenterWorkInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrCenterWorkInfo.table;

	/**
	 * 获取记录ID
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置记录ID
	 */
	public void setId(String id) {
		this.id = id;
	}	
	/**
	 * 获取信息创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}
	/**
	 * 设置信息创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	/**
	 * 获取信息更新时间
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	/**
	 * 设置信息更新时间
	 */
	public Date getUpdateTime() {
		return updateTime;
	}
	/**
	 * 获取信息记录排序号
	 */
	public String getSequence() {
		return sequence;
	}
	/**
	 * 设置信息记录排序号
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	@EntityFieldDescribe( "数据库主键,自动生成." )
	@Id
	@Column( name="xid", length = JpaObject.length_id)
	private String id = createId();

	@EntityFieldDescribe( "创建时间,自动生成." )
	@Index(name = TABLE + "_createTime" )
	@Column( name="xcreateTime" )
	private Date createTime;

	@EntityFieldDescribe( "修改时间,自动生成." )
	@Index(name = TABLE + "_updateTime" )
	@Column( name="xupdateTime" )
	private Date updateTime;

	@EntityFieldDescribe( "列表序号, 由创建时间以及ID组成.在保存时自动生成." )
	@Column( name="xsequence", length = AbstractPersistenceProperties.length_sequence )
	@Index(name = TABLE + "_sequence" )
	private String sequence;
	
	/**
	 * 在执行给定实体的相应 EntityManager 持久操作之前，调用该实体的 @PrePersist 回调方法。
	 */
	@PrePersist
	public void prePersist() throws Exception { 
		Date date = new Date();
		if (null == this.createTime) {
			this.createTime = date;
		}
		this.updateTime = date;
		
		//序列号信息的组成，与排序有关
		if (null == this.sequence) {
			this.sequence = StringUtils.join( DateTools.compact(this.getCreateTime()), this.getId() );
		}
		
		this.onPersist();
	}
	/**
	 * 在对实体数据进行数据库更新操作之前，调用实体的 @PreUpdate 回调方法。
	 */
	@PreUpdate
	public void preUpdate() throws Exception{
		this.updateTime = new Date();
		this.onPersist();
	}

	private void onPersist() throws Exception{
	}
	/* ==================================================================================
	 *                             以上为 JpaObject 默认字段
	 * ================================================================================== */
	
	
	/* ==================================================================================
	 *                             以下为具体不同的业务及数据表字段要求
	 * ================================================================================== */
	@EntityFieldDescribe( "中心标题" )
	@Column( name="xtitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = false)
	private String title = "";

	@EntityFieldDescribe( "工作部署年份" )
	@Column( name="xdeployYear", length = JpaObject.length_16B )
	@Index( name = TABLE + "_deployYear" )
	@CheckPersist( allowEmpty = true )
	private String deployYear = "";
	
	@EntityFieldDescribe( "工作部署月份" )
	@Column(name="xdeployMonth", length = JpaObject.length_16B )
	@Index( name = TABLE + "_deployMonth" )
	@CheckPersist( allowEmpty = true)
	private String deployMonth = "";
	
	@EntityFieldDescribe( "部署者姓名" )
	@Column( name="xdeployerName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true )
	private String deployerName = "";
	
	@EntityFieldDescribe( "部署者身份" )
	@Column( name="xdeployerIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true )
	private String deployerIdentity = "";
	
	@EntityFieldDescribe( "部署者所属组织" )
	@Column( name="xdeployerOrganizationName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true )
	private String deployerOrganizationName = "";
	
	@EntityFieldDescribe( "部署者所属公司" )
	@Column( name="xdeployerCompanyName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true )
	private String deployerCompanyName = "";
	
	@EntityFieldDescribe( "审核者姓名" )
	@Column( name="xauditLeaderName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true )
	private String auditLeaderName = "";
	
	@EntityFieldDescribe( "审核者身份" )
	@Column( name="xauditLeaderIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true )
	private String auditLeaderIdentity = "";
	
	@EntityFieldDescribe( "审核者所属组织" )
	@Column( name="xauditLeaderOrganizationName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true )
	private String auditLeaderOrganizationName = "";
	
	@EntityFieldDescribe( "审核者所属公司" )
	@Column( name="xauditLeaderCompanyName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true )
	private String auditLeaderCompanyName = "";
	
	@EntityFieldDescribe( "创建者姓名" )
	@Column( name="xcreatorName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true )
	private String creatorName = "";
	
	@EntityFieldDescribe( "创建者身份" )
	@Column( name="xcreatorIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true )
	private String creatorIdentity = "";
	
	@EntityFieldDescribe( "创建者所属组织" )
	@Column( name="xcreatorOrganizationName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true )
	private String creatorOrganizationName = "";
	
	@EntityFieldDescribe( "创建者所属公司" )
	@Column( name="xcreatorCompanyName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true )
	private String creatorCompanyName = "";
	
	@EntityFieldDescribe( "中心工作处理状态：草稿|待审核|待确认|执行中|已完成|已撤消" )
	@Column( name="xprocessStatus", length = JpaObject.length_32B )
	@CheckPersist(allowEmpty = true )
	private String processStatus = "草稿";
	
	@EntityFieldDescribe( "中心工作部署日期-字符串，显示用：yyyy-mm-dd" )
	@Column(name="xdeployDateStr", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String deployDateStr = "";
	
	@EntityFieldDescribe( "中心工作默认完成日期" )
	@Column( name="xdefaultCompleteDateLimit" )
	@CheckPersist( allowEmpty = true )
	private Date defaultCompleteDateLimit = null;
	
	@EntityFieldDescribe( "中心工作默认完成日期-字符串，显示用：yyyy-mm-dd" )
	@Column( name="xdefaultCompleteDateLimitStr", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String defaultCompleteDateLimitStr = "";
	
	@EntityFieldDescribe( "中心工作默认工作类别" )
	@Column( name="xdefaultWorkType", length = JpaObject.length_32B )
	@CheckPersist(allowEmpty = true )
	private String defaultWorkType = "";
	
	@EntityFieldDescribe( "中心工作默认工作级别" )
	@Column( name="xdefaultWorkLevel", length = JpaObject.length_32B )
	@CheckPersist(allowEmpty = true )
	private String defaultWorkLevel = "";
	
	@EntityFieldDescribe( "中心工作默认阅知领导(可多值，显示用)" )
	@Column( name="xdefaultLeader", length = JpaObject.length_255B )
	@CheckPersist(allowEmpty = true )
	private String defaultLeader = "";
	
	@EntityFieldDescribe( "中心工作默认阅知领导身份(可多值，计算组织和公司用)" )
	@Column( name="xdefaultLeaderIdentity", length = JpaObject.length_255B )
	@CheckPersist(allowEmpty = true )
	private String defaultLeaderIdentity = "";
	
	@EntityFieldDescribe( "工作汇报审批领导(可多值，显示用)" )
	@Column( name="xreportAuditLeaderName", length = JpaObject.length_255B )
	@CheckPersist(allowEmpty = true )
	private String reportAuditLeaderName = "";
	
	@EntityFieldDescribe( "工作汇报审批领导身份(可多值，计算组织和公司用)" )
	@Column( name="xreportAuditLeaderIdentity", length = JpaObject.length_255B )
	@CheckPersist(allowEmpty = true )
	private String reportAuditLeaderIdentity = "";
	
	@EntityFieldDescribe( "中心工作是否需要审核" )
	@Column( name="xisNeedAudit" )
	@CheckPersist( allowEmpty = true )
	private Boolean isNeedAudit = false;

	@EntityFieldDescribe( "处理状态：正常|已删除" )
	@Column( name="xstatus", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = false )
	private String status = "正常";
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "中心工作描述" )
	@Column( name="xdescription", length = JpaObject.length_2K )
	@CheckPersist( allowEmpty = true )
	private String description = "";
	
	@EntityFieldDescribe( "工作总个数" )
	@Column( name="xworkTotal" )
	@CheckPersist( allowEmpty = true )
	private Long workTotal = 0L;
	
	@EntityFieldDescribe( "执行中工作个数" )
	@Column( name="xprocessingWorkCount" )
	@CheckPersist( allowEmpty = true )
	private Long processingWorkCount = 0L;
	
	@EntityFieldDescribe( "已完成工作个数" )
	@Column( name="xcompletedWorkCount" )
	@CheckPersist( allowEmpty = true )
	private Long completedWorkCount = 0L;
	
	@EntityFieldDescribe( "已超时工作个数" )
	@Column( name="xovertimeWorkCount" )
	@CheckPersist( allowEmpty = true )
	private Long overtimeWorkCount = 0L;
	
	@EntityFieldDescribe( "草稿工作个数" )
	@Column( name="xdraftWorkCount" )
	@CheckPersist( allowEmpty = true )
	private Long draftWorkCount = 0L;
	
	@EntityFieldDescribe( "附件列表" )
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_attachmentList", joinIndex = @Index(name = TABLE + "_attachmentList_join" ) )
	@ElementColumn(length = JpaObject.length_id)
	@ElementIndex(name = TABLE + "_attachmentList_element" )
	@CheckPersist(allowEmpty = true)
	private List<String> attachmentList;
	
	/**
	 * 获取中心工作标题
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * 设置中心工作标题
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 获取部署者姓名
	 * @return
	 */
	public String getDeployerName() {
		return deployerName;
	}
	/**
	 * 设置部署者姓名
	 * @param deployerName
	 */
	public void setDeployerName(String deployerName) {
		this.deployerName = deployerName;
	}
	/**
	 * 获取部署者所属组织名称
	 * @return
	 */
	public String getDeployerOrganizationName() {
		return deployerOrganizationName;
	}
	/**
	 * 设置部署者所属组织名称
	 * @param deployerOrganizationName
	 */
	public void setDeployerOrganizationName(String deployerOrganizationName) {
		this.deployerOrganizationName = deployerOrganizationName;
	}
	/**
	 * 获取部署者所属公司名称
	 * @return
	 */
	public String getDeployerCompanyName() {
		return deployerCompanyName;
	}
	/**
	 * 设置部署者所属公司名称
	 * @param deployerCompanyName
	 */
	public void setDeployerCompanyName(String deployerCompanyName) {
		this.deployerCompanyName = deployerCompanyName;
	}
	/**
	 * 获取创建者姓名
	 * @return
	 */
	public String getCreatorName() {
		return creatorName;
	}
	/**
	 * 设置创建者姓名
	 * @param creatorName
	 */
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	/**
	 * 获取创建者所属组织名称
	 * @return
	 */
	public String getCreatorOrganizationName() {
		return creatorOrganizationName;
	}
	/**
	 * 设置创建者所属组织名称
	 * @param creatorOrganizationName
	 */
	public void setCreatorOrganizationName(String creatorOrganizationName) {
		this.creatorOrganizationName = creatorOrganizationName;
	}
	/**
	 * 获取创建者所属公司名称
	 * @return
	 */
	public String getCreatorCompanyName() {
		return creatorCompanyName;
	}
	/**
	 * 设置创建者所属公司名称
	 * @param creatorCompanyName
	 */
	public void setCreatorCompanyName(String creatorCompanyName) {
		this.creatorCompanyName = creatorCompanyName;
	}
	/**
	 * 获取中心工作信息处理状态：草稿|待确认|执行中|已完成|已撤消
	 * @return
	 */
	public String getProcessStatus() {
		return processStatus;
	}
	/**
	 * 设置中心工作信息处理状态：草稿|待确认|执行中|已完成|已撤消
	 * @param processStatus
	 */
	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}
	/**
	 * 获取中心工作下属工作默认完成日期
	 * @return
	 */
	public Date getDefaultCompleteDateLimit() {
		return defaultCompleteDateLimit;
	}
	/**
	 * 设置中心工作下属工作默认完成日期
	 * @param defaultCompleteDateLimit
	 */
	public void setDefaultCompleteDateLimit(Date defaultCompleteDateLimit) {
		this.defaultCompleteDateLimit = defaultCompleteDateLimit;
	}
	/**
	 * 获取中心工作下属工作默认完成日期（字符串）
	 * @return
	 */
	public String getDefaultCompleteDateLimitStr() {
		return defaultCompleteDateLimitStr;
	}
	/**
	 * 设置中心工作下属工作默认完成日期（字符串）
	 * @param defaultCompleteDateLimitStr
	 */
	public void setDefaultCompleteDateLimitStr(String defaultCompleteDateLimitStr) {
		this.defaultCompleteDateLimitStr = defaultCompleteDateLimitStr;
	}
	/**
	 * 获取中心工作下属工作默认工作类别
	 * @return
	 */
	public String getDefaultWorkType() {
		return defaultWorkType;
	}
	/**
	 * 设置中心工作下属工作默认工作类别
	 * @param defaultWorkType
	 */
	public void setDefaultWorkType(String defaultWorkType) {
		this.defaultWorkType = defaultWorkType;
	}
	/**
	 * 获取中心工作下属工作默认工作级别
	 * @return
	 */
	public String getDefaultWorkLevel() {
		return defaultWorkLevel;
	}
	/**
	 * 设置中心工作下属工作默认工作级别
	 * @param defaultWorkLevel
	 */
	public void setDefaultWorkLevel(String defaultWorkLevel) {
		this.defaultWorkLevel = defaultWorkLevel;
	}
	/**
	 * 获取中心工作下属工作默认阅知领导
	 * @return
	 */
	public String getDefaultLeader() {
		return defaultLeader;
	}
	/**
	 * 设置中心工作下属工作默认阅知领导
	 * @param defaultLeader
	 */
	public void setDefaultLeader(String defaultLeader) {
		this.defaultLeader = defaultLeader;
	}
	/**
	 * 获取工作部署年份
	 * @return
	 */
	public String getDeployYear() {
		return deployYear;
	}
	/**
	 * 设置工作部署年份
	 * @param deployYear
	 */
	public void setDeployYear(String deployYear) {
		this.deployYear = deployYear;
	}
	/**
	 * 获取工作部署月份
	 * @return
	 */
	public String getDeployMonth() {
		return deployMonth;
	}
	/**
	 * 设置工作部署月份
	 * @param deployMonth
	 */
	public void setDeployMonth(String deployMonth) {
		this.deployMonth = deployMonth;
	}
	/**
	 * 获取审核领导姓名
	 * @return
	 */
	public String getAuditLeaderName() {
		return auditLeaderName;
	}
	/**
	 * 设置审核领导姓名
	 * @param auditLeaderName
	 */
	public void setAuditLeaderName(String auditLeaderName) {
		this.auditLeaderName = auditLeaderName;
	}
	/**
	 * 获取审核领导所属组织名称
	 * @return
	 */
	public String getAuditLeaderOrganizationName() {
		return auditLeaderOrganizationName;
	}
	/**
	 * 设置审核领导所属组织名称
	 * @param auditLeaderOrganizationName
	 */
	public void setAuditLeaderOrganizationName(String auditLeaderOrganizationName) {
		this.auditLeaderOrganizationName = auditLeaderOrganizationName;
	}
	/**
	 * 获取审核领导所属公司名称
	 * @return
	 */
	public String getAuditLeaderCompanyName() {
		return auditLeaderCompanyName;
	}
	/**
	 * 设置审核领导所属公司名称
	 * @param auditLeaderCompanyName
	 */
	public void setAuditLeaderCompanyName(String auditLeaderCompanyName) {
		this.auditLeaderCompanyName = auditLeaderCompanyName;
	}
	/**
	 * 获取是否需要审核
	 * @return
	 */
	public Boolean getIsNeedAudit() {
		return isNeedAudit;
	}
	/**
	 * 设置是否需要审核
	 * @param isNeedAudit
	 */
	public void setIsNeedAudit(Boolean isNeedAudit) {
		this.isNeedAudit = isNeedAudit;
	}
	/**
	 * 获取信息状态：正常|已删除
	 * @return
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * 设置信息状态：正常|已删除
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * 获取中心工作描述
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * 设置中心工作描述
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
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
	public String getAuditLeaderIdentity() {
		return auditLeaderIdentity;
	}
	public void setAuditLeaderIdentity(String auditLeaderIdentity) {
		this.auditLeaderIdentity = auditLeaderIdentity;
	}
	public String getDefaultLeaderIdentity() {
		return defaultLeaderIdentity;
	}
	public void setDefaultLeaderIdentity(String defaultLeaderIdentity) {
		this.defaultLeaderIdentity = defaultLeaderIdentity;
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
}