package com.x.okr.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

/**
 * 中心工作汇报情况统计信息实体类
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrStatisticReportContent.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrStatisticReportContent extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrStatisticReportContent.table;
	
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
	@EntityFieldDescribe( "中心工作ID" )
	@Column( name="xcenterId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true)
	private String centerId = "";
	
	@EntityFieldDescribe( "中心标题" )
	@Column( name="xcenterTitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = false)
	private String centerTitle = "";
	
	@EntityFieldDescribe( "工作ID" )
	@Column( name="xworkId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true)
	private String workId = "";
	
	@EntityFieldDescribe( "上级工作ID" )
	@Column( name="xparentId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true)
	private String parentId = null;
	
	@EntityFieldDescribe( "工作标题" )
	@Column( name="xworkTitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = false)
	private String workTitle = "";
	
	@EntityFieldDescribe( "工作汇报ID" )
	@Column( name="xreportId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true)
	private String reportId = "";
	
	@EntityFieldDescribe( "工作类别" )
	@Column( name="xworkType", length = JpaObject.length_32B )
	@CheckPersist(allowEmpty = true )
	private String workType = "";
	
	@EntityFieldDescribe( "工作级别" )
	@Column( name="xworkLevel", length = JpaObject.length_32B )
	@CheckPersist(allowEmpty = true )
	private String workLevel = "";
	
	@EntityFieldDescribe( "统计周期：每周统计|每月统计" )
	@Column( name="xcycleType", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true)
	private String cycleType = "";

	@EntityFieldDescribe( "统计时间." )
	@Column( name="xstatisticTime" )
	@CheckPersist( allowEmpty = true)
	private Date statisticTime = null;
	
	@EntityFieldDescribe( "统计时间标识." )
	@Column( name="xstatisticTimeFlag", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true)
	private String statisticTimeFlag = null;

	@EntityFieldDescribe( "统计年份" )
	@Column( name="xstatisticYear", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = true)
	private Integer statisticYear = null;
	
	@EntityFieldDescribe( "统计月份" )
	@Column( name="xstatisticMonth", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = true)
	private Integer statisticMonth = null;
	
	@EntityFieldDescribe( "统计周数" )
	@Column( name="xstatisticWeek" )
	@CheckPersist( allowEmpty = true)
	private Integer statisticWeek = null;
	
	@EntityFieldDescribe( "处理状态：正常|已删除" )
	@Column( name="xstatus", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = true )
	private String status = "正常";
	
	@EntityFieldDescribe( "主责人姓名" )
	@Column(name="xresponsibilityEmployeeName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String responsibilityEmployeeName = "";
	
	@EntityFieldDescribe( "主责人身份" )
	@Column(name="xresponsibilityIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String responsibilityIdentity = "";
	
	@EntityFieldDescribe( "主责人所属组织" )
	@Column(name="xresponsibilityOrganizationName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String responsibilityOrganizationName = "";
	
	@EntityFieldDescribe( "主责人所属公司" )
	@Column(name="xresponsibilityCompanyName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String responsibilityCompanyName = "";
	
	@EntityFieldDescribe( "工作是否已完成" )
	@Column(name="xisCompleted" )
	@CheckPersist( allowEmpty = true )
	private Boolean isCompleted = false;
	
	@EntityFieldDescribe( "工作是否已超期" )
	@Column(name="xisOverTime" )
	@CheckPersist( allowEmpty = true )
	private Boolean isOverTime = false;
	
	@EntityFieldDescribe( "工作处理状态：草稿|待确认|执行中|已超期|已完成|已撤消" )
	@Column(name="xworkProcessStatus", length = JpaObject.length_16B )
	@CheckPersist(  allowEmpty = true )
	private String workProcessStatus = "草稿";
	
	@EntityFieldDescribe( "工作汇报状态" )
	@Column(name="xreportStatus", length = JpaObject.length_32B )
	@CheckPersist(  allowEmpty = true )
	private String reportStatus = "未提交汇报";
	
	@EntityFieldDescribe( "周期汇报时间：每月的几号(1-31)，每周的星期几(1-7)，启动时间由系统配置设定，比如：10:00" )
	@Column(name="xreportDayInCycle" )
	@CheckPersist( allowEmpty = true )
	private Integer reportDayInCycle = 0;
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "截止当前完成情况" )
	@Column(name="xprogressDescription", length = JpaObject.length_2K )
	@CheckPersist( allowEmpty = true)
	private String progressDescription = "";
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "后续工作计划" )
	@Column(name="xworkPlan", length = JpaObject.length_2K )
	@CheckPersist( allowEmpty = true)
	private String workPlan = "";
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "工作汇报备注信息" )
	@Column(name="xmemo", length = JpaObject.length_2K )
	@CheckPersist( allowEmpty = true)
	private String memo = "";
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "工作要点及需求" )
	@Column(name="xworkPointAndRequirements", length = JpaObject.length_2K )
	@CheckPersist( allowEmpty = true)
	private String workPointAndRequirements  = "";

	@Lob
	@EntityFieldDescribe( "管理员督办信息" )
	@Column(name="xadminSuperviseInfo", length = JpaObject.length_2K )
	@CheckPersist( allowEmpty = true)
	private String adminSuperviseInfo = "";
	
	@Lob
	@EntityFieldDescribe( "领导处理意见" )
	@Column(name="xopinion", length = JpaObject.length_2K )
	@CheckPersist( allowEmpty = true)
	private String opinion = "";

	public String getCenterId() {
		return centerId;
	}
	public String getCenterTitle() {
		return centerTitle;
	}
	public String getWorkId() {
		return workId;
	}
	public String getWorkTitle() {
		return workTitle;
	}
	public String getReportId() {
		return reportId;
	}
	public String getWorkType() {
		return workType;
	}
	public String getWorkLevel() {
		return workLevel;
	}
	public String getCycleType() {
		return cycleType;
	}
	public Date getStatisticTime() {
		return statisticTime;
	}
	public String getStatus() {
		return status;
	}
	public String getResponsibilityEmployeeName() {
		return responsibilityEmployeeName;
	}
	public String getResponsibilityIdentity() {
		return responsibilityIdentity;
	}
	public String getResponsibilityOrganizationName() {
		return responsibilityOrganizationName;
	}
	public String getResponsibilityCompanyName() {
		return responsibilityCompanyName;
	}
	public Boolean getIsCompleted() {
		return isCompleted;
	}
	public Boolean getIsOverTime() {
		return isOverTime;
	}
	public String getWorkProcessStatus() {
		return workProcessStatus;
	}
	public Integer getReportDayInCycle() {
		return reportDayInCycle;
	}
	public String getProgressDescription() {
		return progressDescription;
	}
	public String getWorkPlan() {
		return workPlan;
	}
	public String getMemo() {
		return memo;
	}
	public String getWorkPointAndRequirements() {
		return workPointAndRequirements;
	}
	public String getAdminSuperviseInfo() {
		return adminSuperviseInfo;
	}
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}
	public void setCenterTitle(String centerTitle) {
		this.centerTitle = centerTitle;
	}
	public void setWorkId(String workId) {
		this.workId = workId;
	}
	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	public void setWorkType(String workType) {
		this.workType = workType;
	}
	public void setWorkLevel(String workLevel) {
		this.workLevel = workLevel;
	}
	public void setCycleType(String cycleType) {
		this.cycleType = cycleType;
	}
	public void setStatisticTime(Date statisticTime) {
		this.statisticTime = statisticTime;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setResponsibilityEmployeeName(String responsibilityEmployeeName) {
		this.responsibilityEmployeeName = responsibilityEmployeeName;
	}
	public void setResponsibilityIdentity(String responsibilityIdentity) {
		this.responsibilityIdentity = responsibilityIdentity;
	}
	public void setResponsibilityOrganizationName(String responsibilityOrganizationName) {
		this.responsibilityOrganizationName = responsibilityOrganizationName;
	}
	public void setResponsibilityCompanyName(String responsibilityCompanyName) {
		this.responsibilityCompanyName = responsibilityCompanyName;
	}
	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}
	public void setIsOverTime(Boolean isOverTime) {
		this.isOverTime = isOverTime;
	}
	public void setWorkProcessStatus(String workProcessStatus) {
		this.workProcessStatus = workProcessStatus;
	}
	public void setReportDayInCycle(Integer reportDayInCycle) {
		this.reportDayInCycle = reportDayInCycle;
	}
	public void setProgressDescription(String progressDescription) {
		this.progressDescription = progressDescription;
	}
	public void setWorkPlan( String workPlan ) {
		this.workPlan = workPlan;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public void setWorkPointAndRequirements(String workPointAndRequirements) {
		this.workPointAndRequirements = workPointAndRequirements;
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
	public Integer getStatisticYear() {
		return statisticYear;
	}
	public Integer getStatisticMonth() {
		return statisticMonth;
	}
	public Integer getStatisticWeek() {
		return statisticWeek;
	}
	public void setStatisticYear(Integer statisticYear) {
		this.statisticYear = statisticYear;
	}
	public void setStatisticMonth(Integer statisticMonth) {
		this.statisticMonth = statisticMonth;
	}
	public void setStatisticWeek(Integer statisticWeek) {
		this.statisticWeek = statisticWeek;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getReportStatus() {
		return reportStatus;
	}
	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}
	public String getStatisticTimeFlag() {
		return statisticTimeFlag;
	}
	public void setStatisticTimeFlag(String statisticTimeFlag) {
		this.statisticTimeFlag = statisticTimeFlag;
	}
	
}