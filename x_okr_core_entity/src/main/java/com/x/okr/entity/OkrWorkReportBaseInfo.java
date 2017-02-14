package com.x.okr.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
 * 工作汇报基础信息管理实体类
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrWorkReportBaseInfo.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrWorkReportBaseInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrWorkReportBaseInfo.table;

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
	public void prePersist() {
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
	public void preUpdate() {
		this.updateTime = new Date();
		this.onPersist();
	}

	private void onPersist() {
	}
	/* ==================================================================================
	 *                             以上为 JpaObject 默认字段
	 * ================================================================================== */
	
	
	/* ==================================================================================
	 *                             以下为具体不同的业务及数据表字段要求
	 * ================================================================================== */
	@EntityFieldDescribe( "汇报提交时间." )
	@Column( name="xsubmitTime" )
	private Date submitTime;
	
	@EntityFieldDescribe( "工作标题" )
	@Column(name="xworkTitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = false)
	private String workTitle = null;
	
	@EntityFieldDescribe( "中心工作标题" )
	@Column(name="xcenterTitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = false)
	private String centerTitle = null;
	
	@EntityFieldDescribe( "所属中心工作ID" )
	@Column( name="xcenterId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = false)
	private String centerId = null;
	
	@EntityFieldDescribe( "所属工作ID" )
	@Column( name="xworkId", length = JpaObject.length_id )
	@Index(name = TABLE + "_workId" )
	@CheckPersist( allowEmpty = false)
	private String workId = null;
	
	@EntityFieldDescribe( "工作类别" )
	@Column(name="xworkType", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String workType = "";
	
	@EntityFieldDescribe( "工作汇报标题" )
	@Column(name="xtitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = false)
	private String title = null;
	
	@EntityFieldDescribe( "工作汇报短标题" )
	@Column(name="xshortTitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true)
	private String shortTitle = null;
	
	@EntityFieldDescribe( "工作汇报当前环节" )
	@Column(name="xactivityName", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true)
	private String activityName = "草稿";
	
	@EntityFieldDescribe( "工作汇报次序" )
	@Column(name="xreportCount" )
	@CheckPersist( allowEmpty = true)
	private Integer reportCount = 0;

	@EntityFieldDescribe( "汇报者姓名" )
	@Column(name="xreporterName", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_reporterName" )
	@CheckPersist( allowEmpty = true )
	private String reporterName = null;
	
	@EntityFieldDescribe( "汇报者身份" )
	@Column(name="xreporterIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_reporterIdentity" )
	@CheckPersist( allowEmpty = true )
	private String reporterIdentity = null;
	
	@EntityFieldDescribe( "汇报者所属组织" )
	@Column(name="xreporterOrganizationName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String reporterOrganizationName = null;
	
	@EntityFieldDescribe( "汇报者所属公司" )
	@Column(name="xreporterCompanyName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String reporterCompanyName = null;	
	
	@EntityFieldDescribe( "创建者姓名" )
	@Column(name="xcreatorName", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_creatorName" )
	@CheckPersist( allowEmpty = true )
	private String creatorName = null;
	
	@EntityFieldDescribe( "创建者身份" )
	@Column(name="xcreatorIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_creatorIdentity" )
	@CheckPersist( allowEmpty = true )
	private String creatorIdentity = null;
	
	@EntityFieldDescribe( "创建者所属组织" )
	@Column(name="xcreatorOrganizationName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String creatorOrganizationName = null;
	
	@EntityFieldDescribe( "创建者所属公司" )
	@Column(name="xcreatorCompanyName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String creatorCompanyName = null;
	
	@EntityFieldDescribe( "工作是否已经完成" )
	@Column(name="xisWorkCompleted" )
	private Boolean isWorkCompleted = false;
	
	@EntityFieldDescribe( "工作完成进度 % " )
	@Column(name="xprogressPercent" )
	private Double progressPercent = 0.0;
	
	@EntityFieldDescribe( "汇报处理状态：草稿|管理员督办|领导批示|已完成" )
	@Column(name="xprocessStatus", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = true )
	private String processStatus = "草稿";
	
	@EntityFieldDescribe( "汇报信息状态：正常|已删除" )
	@Column(name="xstatus", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = true )
	private String status = "正常";
	
	@EntityFieldDescribe( "汇报处理类别：审批|阅知" )
	@Column(name="xprocessType", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = true )
	private String processType = "";
	
	@EntityFieldDescribe( "当前处理级别" )
	@Column(name="xcurrentProcessLevel" )
	@Index(name = TABLE + "_currentProcessLevel" )
	@CheckPersist( allowEmpty = true )
	private Integer currentProcessLevel = 0;
	
	@EntityFieldDescribe( "当前处理人姓名" )
	@Column(name="xcurrentProcessorName", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_currentProcessorName" )
	@CheckPersist( allowEmpty = true )
	private String currentProcessorName = null;
	
	@EntityFieldDescribe( "当前处理人身份" )
	@Column(name="xcurrentProcessorIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String currentProcessorIdentity = null;
	
	@EntityFieldDescribe( "当前处理人所属组织" )
	@Column(name="xcurrentProcessorOrganizationName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String currentProcessorOrganizationName = null;
	
	@EntityFieldDescribe( "当前处理人所属公司" )
	@Column(name="xcurrentProcessorCompanyName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String currentProcessorCompanyName = null;
	
	@EntityFieldDescribe( "是否需要工作管理员审核" )
	@Column(name="xneedAdminAudit" )
	@CheckPersist( allowEmpty = true )
	private Boolean needAdminAudit = false;
	
	@EntityFieldDescribe( "是否需要领导批示" )
	@Column(name="xneedLeaderRead" )
	@CheckPersist( allowEmpty = true )
	private Boolean needLeaderRead = false;
	
	@EntityFieldDescribe( "工作管理员姓名" )
	@Column(name="xworkAdminName", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String workAdminName = null;
	
	@EntityFieldDescribe( "工作管理员身份" )
	@Column(name="xworkAdminIdentity", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String workAdminIdentity = null;
	
	@EntityFieldDescribe( "批示领导姓名列表" )
	@Column(name="xreadLeadersName", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String readLeadersName = null;
	
	@EntityFieldDescribe( "批示领导身份列表" )
	@Column(name="xreadLeadersIdentity", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String readLeadersIdentity = null;
	
	@EntityFieldDescribe( "汇报工作流处理方式" )
	@Column(name="xreportWorkflowType", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String reportWorkflowType = null;
	
	@EntityFieldDescribe( "说明备注" )
	@Column(name="xdescription", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String description = null;
	
	@EntityFieldDescribe( "附件列表" )
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_attachmentList", joinIndex = @Index(name = TABLE + "_attachmentList_join" ) )
	@ElementColumn(length = JpaObject.length_id)
	@ElementIndex(name = TABLE + "_attachmentList_element" )
	@CheckPersist(allowEmpty = true)
	private List<String> attachmentList;

	/**
	 * 获取所属工作标题
	 * @return
	 */
	public String getWorkTitle() {
		return workTitle;
	}
	/**
	 * 设置所属工作标题
	 * @param workTitle
	 */
	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}
	/**
	 * 获取所属中心工作ID
	 * @return
	 */
	public String getCenterId() {
		return centerId;
	}
	/**
	 * 设置所属中心工作ID
	 * @param centerId
	 */
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}
	/**
	 * 获取所属工作ID
	 * @return
	 */
	public String getWorkId() {
		return workId;
	}
	/**
	 * 设置所属工作ID
	 * @param workId
	 */
	public void setWorkId(String workId) {
		this.workId = workId;
	}
	/**
	 * 获取汇报标题
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * 设置汇报标题
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 获取汇报短标题
	 * @return
	 */
	public String getShortTitle() {
		return shortTitle;
	}
	/**
	 * 设置汇报短标题
	 * @param shortTitle
	 */
	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}
	/**
	 * 获取汇报次序
	 * @return
	 */
	public Integer getReportCount() {
		return reportCount;
	}
	/**
	 * 设置汇报次序
	 * @param reportCount
	 */
	public void setReportCount(Integer reportCount) {
		this.reportCount = reportCount;
	}
	/**
	 * 获取汇报者姓名
	 * @return
	 */
	public String getReporterName() {
		return reporterName;
	}
	/**
	 * 设置汇报者姓名
	 * @param reporterName
	 */
	public void setReporterName(String reporterName) {
		this.reporterName = reporterName;
	}
	/**
	 * 获取汇报者所属组织名称
	 * @return
	 */
	public String getReporterOrganizationName() {
		return reporterOrganizationName;
	}
	/**
	 * 设置汇报者所属组织名称
	 * @param reporterOrganizationName
	 */
	public void setReporterOrganizationName(String reporterOrganizationName) {
		this.reporterOrganizationName = reporterOrganizationName;
	}
	/**
	 * 获取汇报者所属公司名称
	 * @return
	 */
	public String getReporterCompanyName() {
		return reporterCompanyName;
	}
	/**
	 * 设置汇报者所属公司名称
	 * @param reporterCompanyName
	 */
	public void setReporterCompanyName(String reporterCompanyName) {
		this.reporterCompanyName = reporterCompanyName;
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
	 * 获取工作是否已经完成
	 * @return
	 */
	public Boolean getIsWorkCompleted() {
		return isWorkCompleted;
	}
	/**
	 * 设置工作是否已经完成
	 * @param isWorkCompleted
	 */
	public void setIsWorkCompleted(Boolean isWorkCompleted) {
		this.isWorkCompleted = isWorkCompleted;
	}
	/**
	 * 获取工作进度百分比
	 * @return
	 */
	public Double getProgressPercent() {
		return progressPercent;
	}
	/**
	 * 设置工作进度百分比
	 * @param progressPercent
	 */
	public void setProgressPercent(Double progressPercent) {
		this.progressPercent = progressPercent;
	}
	/**
	 * 获取汇报信息状态：正常|已删除
	 * @return
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * 设置汇报信息状态：正常|已删除
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * 获取汇报当前处理者姓名
	 * @return
	 */
	public String getCurrentProcessorName() {
		return currentProcessorName;
	}
	/**
	 * 设置汇报当前处理者姓名
	 * @param currentProcessorName
	 */
	public void setCurrentProcessorName(String currentProcessorName) {
		this.currentProcessorName = currentProcessorName;
	}
	/**
	 * 获取汇报当前处理者所属组织名称
	 * @return
	 */
	public String getCurrentProcessorOrganizationName() {
		return currentProcessorOrganizationName;
	}
	/**
	 * 设置汇报当前处理者所属组织名称
	 * @param currentProcessorOrganizationName
	 */
	public void setCurrentProcessorOrganizationName(String currentProcessorOrganizationName) {
		this.currentProcessorOrganizationName = currentProcessorOrganizationName;
	}
	/**
	 * 获取汇报当前处理者所属公司名称
	 * @return
	 */
	public String getCurrentProcessorCompanyName() {
		return currentProcessorCompanyName;
	}
	/**
	 * 设置汇报当前处理者所属公司名称
	 * @param currentProcessorCompanyName
	 */
	public void setCurrentProcessorCompanyName(String currentProcessorCompanyName) {
		this.currentProcessorCompanyName = currentProcessorCompanyName;
	}
	/**
	 * 获取汇报处理类别：审批|阅知
	 * @return
	 */
	public String getProcessType() {
		return processType;
	}
	/**
	 * 设置汇报处理类别：审批|阅知
	 * @param processType
	 */
	public void setProcessType(String processType) {
		this.processType = processType;
	}
	
	/**
	 * 获取中心工作标题
	 * @return
	 */
	public String getCenterTitle() {
		return centerTitle;
	}
	/**
	 * 设置中心工作标题
	 * @param centerTitle
	 */
	public void setCenterTitle(String centerTitle) {
		this.centerTitle = centerTitle;
	}
	/**
	 * 获取汇报处理状态：草稿|审阅中|汇报人确认|已归档|已撤回
	 * @return
	 */
	public String getProcessStatus() {
		return processStatus;
	}
	/**
	 * 设置汇报处理状态：草稿|审阅中|汇报人确认|已归档|已撤回
	 * @param processStatus
	 */
	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}
	/**
	 * 获取是否需要工作管理员审核
	 * @return
	 */
	public Boolean getNeedAdminAudit() {
		return needAdminAudit;
	}
	/**
	 * 设置是否需要工作管理员审核
	 * @param needAdminAudit
	 */
	public void setNeedAdminAudit(Boolean needAdminAudit) {
		this.needAdminAudit = needAdminAudit;
	}
	/**
	 * 获取工作管理员姓名
	 * @return
	 */
	public String getWorkAdminName() {
		return workAdminName;
	}
	/**
	 * 设置工作管理员姓名
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
	public String getCurrentProcessorIdentity() {
		return currentProcessorIdentity;
	}
	public void setCurrentProcessorIdentity(String currentProcessorIdentity) {
		this.currentProcessorIdentity = currentProcessorIdentity;
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
	public String getReadLeadersName() {
		return readLeadersName;
	}
	public void setReadLeadersName(String readLeadersName) {
		this.readLeadersName = readLeadersName;
	}
	public String getReadLeadersIdentity() {
		return readLeadersIdentity;
	}
	public void setReadLeadersIdentity(String readLeadersIdentity) {
		this.readLeadersIdentity = readLeadersIdentity;
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
	
}