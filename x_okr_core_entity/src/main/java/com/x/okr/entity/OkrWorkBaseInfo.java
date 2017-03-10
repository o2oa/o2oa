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
 * 工作基础信息管理实体类
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrWorkBaseInfo.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrWorkBaseInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrWorkBaseInfo.table;

	/**
	 * 获取明细记录ID
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置明细记录ID
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
	@EntityFieldDescribe( "工作标题" )
	@Column(name="xtitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = false)
	private String title = "";
	
	@EntityFieldDescribe( "中心工作ID" )
	@Column( name="xcenterId", length = JpaObject.length_id )
	@Index(name = TABLE + "_centerId" )
	@CheckPersist( allowEmpty = false)
	private String centerId = "";
	
	@EntityFieldDescribe( "中心工作标题" )
	@Column(name="xcenterTitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true)
	private String centerTitle = "";
	
	@EntityFieldDescribe( "上级工作ID" )
	@Column( name="xparentWorkId", length = JpaObject.length_id )
	@Index(name = TABLE + "_parentWorkId" )
	@CheckPersist( allowEmpty = true)
	private String parentWorkId = "";
	
	@EntityFieldDescribe( "上级工作标题" )
	@Column(name="xparentWorkTitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true)
	private String parentWorkTitle = "";	

	@EntityFieldDescribe( "工作期限类型:短期工作(不需要自动启动定期汇报) | 长期工作（需要自动启动定期汇报）" )
	@Column(name="xworkDateTimeType", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = true)
	private String workDateTimeType = "长期工作";
	
	@EntityFieldDescribe( "工作部署年份" )
	@Column(name="xdeployYear", length = JpaObject.length_16B )
	@Index(name = TABLE + "_deployYear" )
	@CheckPersist( allowEmpty = true)
	private String deployYear = "";
	
	@EntityFieldDescribe( "工作部署月份" )
	@Column(name="xdeployMonth", length = JpaObject.length_16B )
	@Index(name = TABLE + "_deployMonth" )
	@CheckPersist( allowEmpty = true)
	private String deployMonth = "";
	
	@EntityFieldDescribe( "部署者姓名" )
	@Column(name="xdeployerName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String deployerName = "";
	
	@EntityFieldDescribe( "部署者姓名" )
	@Column(name="xdeployerIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String deployerIdentity = "";
	
	@EntityFieldDescribe( "部署者所属组织" )
	@Column(name="xdeployerOrganizationName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String deployerOrganizationName = "";
	
	@EntityFieldDescribe( "部署者所属公司" )
	@Column(name="xdeployerCompanyName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String deployerCompanyName = "";
	
	@EntityFieldDescribe( "创建者姓名" )
	@Column(name="xcreatorName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String creatorName = "";
	
	@EntityFieldDescribe( "创建者姓名" )
	@Column(name="xcreatorIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String creatorIdentity = "";
	
	@EntityFieldDescribe( "创建者所属组织" )
	@Column(name="xcreatorOrganizationName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String creatorOrganizationName = "";
	
	@EntityFieldDescribe( "创建者所属公司" )
	@Column(name="xcreatorCompanyName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String creatorCompanyName = "";
	
	@EntityFieldDescribe( "工作部署日期-字符串，显示用：yyyy-mm-dd" )
	@Column(name="xdeployDateStr", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String deployDateStr = "";
	
	@EntityFieldDescribe( "工作确认日期-字符串，显示用：yyyy-mm-dd" )
	@Column(name="xconfirmDateStr", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String confirmDateStr = "";
	
	@EntityFieldDescribe( "工作完成日期" )
	@Column(name="xcompleteDateLimit" )
	@CheckPersist( allowEmpty = true )
	private Date completeDateLimit = null;
	
	@EntityFieldDescribe( "工作完成日期-字符串，显示用：yyyy-mm-dd" )
	@Column(name="xcompleteDateLimitStr", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String completeDateLimitStr = "";
	
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

	@EntityFieldDescribe( "协助人姓名，可能多值" )
	@Column(name="xcooperateEmployeeName", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String cooperateEmployeeName = "";
	
	@EntityFieldDescribe( "协助人身份，可能多值" )
	@Column(name="xcooperateIdentity", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String cooperateIdentity = "";
	
	@EntityFieldDescribe( "协助人所属组织，可能多值" )
	@Column(name="xcooperateOrganizationName", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String cooperateOrganizationName = "";
	
	@EntityFieldDescribe( "协助人所属公司，可能多值" )
	@Column(name="xcooperateCompanyName", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String cooperateCompanyName = "";
	
	@EntityFieldDescribe( "阅知领导身份，可能多值" )
	@Column(name="xreadLeaderIdentity", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String readLeaderIdentity = "";
	
	@EntityFieldDescribe( "阅知领导，可能多值" )
	@Column(name="xreadLeaderName", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String readLeaderName = "";
	
	@EntityFieldDescribe( "阅知领导所属组织，可能多值" )
	@Column(name="xreadLeaderOrganizationName", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String readLeaderOrganizationName = "";
	
	@EntityFieldDescribe( "阅知领导所属公司，可能多值" )
	@Column(name="xreadLeaderCompanyName", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String readLeaderCompanyName = "";
	
	@EntityFieldDescribe( "工作类别" )
	@Column(name="xworkType", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String workType = "";
	
	@EntityFieldDescribe( "工作级别" )
	@Column(name="xworkLevel", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String workLevel = "";
	
	@EntityFieldDescribe( "工作进度" )
	@Column(name="xoverallProgress" )
	private Double overallProgress = 0.0;
	
	@EntityFieldDescribe( "工作处理状态：草稿|待确认|执行中|已超期|已完成|已撤消" )
	@Column(name="xworkProcessStatus", length = JpaObject.length_16B )
	@CheckPersist(  allowEmpty = true )
	private String workProcessStatus = "草稿";
	
	@EntityFieldDescribe( "工作是否已超期" )
	@Column(name="xisOverTime" )
	@CheckPersist( allowEmpty = true )
	private Boolean isOverTime = false;
	
	@EntityFieldDescribe( "工作是否已完成" )
	@Column(name="xisCompleted" )
	@CheckPersist( allowEmpty = true )
	private Boolean isCompleted = false;

	@EntityFieldDescribe( "上一次汇报时间" )
	@Column(name="xlastReportTime", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private Date lastReportTime = null;
	
	@EntityFieldDescribe( "下一次汇报时间" )
	@Column(name="xnextReportTime", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private Date nextReportTime = null;
	
	@EntityFieldDescribe( "已汇报次数" )
	@Column(name="xreportCount" )
	private Integer reportCount = 0;
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "汇报时间队列" )
	@Column(name="xreportTimeQue", length = JpaObject.length_2K )
	@CheckPersist( allowEmpty = true )
	private String reportTimeQue = null;
	
	@EntityFieldDescribe( "汇报周期:不需要汇报|每月汇报|每周汇报" )
	@Column(name="xreportCycle", length = JpaObject.length_32B )
	@CheckPersist(  allowEmpty = true )
	private String reportCycle = null;
	
	@EntityFieldDescribe( "是否需要定期汇报" )
	@Column(name="xisNeedReport" )
	@CheckPersist( allowEmpty = true )
	private Boolean isNeedReport = true;
	
	@EntityFieldDescribe( "周期汇报时间：每月的几号(1-31)，每周的星期几(1-7)，启动时间由系统配置设定，比如：10:00" )
	@Column(name="xreportDayInCycle" )
	@CheckPersist( allowEmpty = true )
	private Integer reportDayInCycle = 0;
	
	@EntityFieldDescribe( "工作部署级别" )
	@Column(name="xworkAuditLevel" )
	private Integer workAuditLevel = 1;
	
	@EntityFieldDescribe( "处理状态：正常|已删除" )
	@Column(name="xstatus", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = true )
	private String status = "正常";
	
	@EntityFieldDescribe( "工作汇报是否需要管理补充信息" )
	@Column( name="xreportNeedAdminAudit" )
	@CheckPersist( allowEmpty = true )
	private Boolean reportNeedAdminAudit = false;
	
	@EntityFieldDescribe( "工作管理员姓名" )
	@Column( name="xreportAdminName", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String reportAdminName = null;
	
	@EntityFieldDescribe( "工作管理员身份" )
	@Column( name="xreportAdminIdentity", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String reportAdminIdentity = null;
	
	@EntityFieldDescribe( "工作详细描述, 事项分解" )
	@Column(name="xshortWorkDetail", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true)
	private String shortWorkDetail = "";
	
	@EntityFieldDescribe( "职责描述" )
	@Column(name="xshortDutyDescription", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true)
	private String shortDutyDescription = "";
	
	@EntityFieldDescribe( "具体行动举措" )
	@Column(name="xshortProgressAction", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true)
	private String shortProgressAction = "";
	
	@EntityFieldDescribe( "里程碑标志说明" )
	@Column(name="xshortLandmarkDescription", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true)
	private String shortLandmarkDescription = "";
	
	@EntityFieldDescribe( "交付成果说明" )
	@Column(name="xshortResultDescription", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true)
	private String shortResultDescription = "";
	
	@EntityFieldDescribe( "重点事项说明" )
	@Column(name="xshortMajorIssuesDescription", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true)
	private String shortMajorIssuesDescription = "";
	
	@EntityFieldDescribe( "进展计划时限说明" )
	@Column(name="xshortProgressPlan", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true)
	private String shortProgressPlan = "";
	
	@EntityFieldDescribe( "工作进展分析时间" )
	@Column(name="xprogressAnalyseTime", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true)
	private String progressAnalyseTime = "";
	
	@EntityFieldDescribe( "附件列表" )
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_attachmentList", joinIndex = @Index(name = TABLE + "_attachmentList_join" ) )
	@ElementColumn(length = JpaObject.length_id)
	@ElementIndex(name = TABLE + "_attachmentList_element" )
	@CheckPersist(allowEmpty = true)
	private List<String> attachmentList;
	
	/**
	 * 获取工作标题
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * 设置工作标题
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
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
	 * 获取上级工作ID
	 * @return
	 */
	public String getParentWorkId() {
		return parentWorkId;
	}
	/**
	 * 设置上级工作ID
	 * @param parentWorkId
	 */
	public void setParentWorkId(String parentWorkId) {
		this.parentWorkId = parentWorkId;
	}
	/**
	 * 获取上级工作标题
	 * @return
	 */
	public String getParentWorkTitle() {
		return parentWorkTitle;
	}
	/**
	 * 设置上级工作标题
	 * @param parentWorkTitle
	 */
	public void setParentWorkTitle(String parentWorkTitle) {
		this.parentWorkTitle = parentWorkTitle;
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
	 * 获取部署日期
	 * @return
	 */
	public String getDeployDateStr() {
		return deployDateStr;
	}
	/**
	 * 设置部署日期
	 * @param deployDateStr
	 */
	public void setDeployDateStr(String deployDateStr) {
		this.deployDateStr = deployDateStr;
	}
	/**
	 * 获取工作确认日期
	 * @return
	 */
	public String getConfirmDateStr() {
		return confirmDateStr;
	}
	/**
	 * 设置工作确认日期
	 * @param confirmDateStr
	 */
	public void setConfirmDateStr(String confirmDateStr) {
		this.confirmDateStr = confirmDateStr;
	}
	/**
	 * 获取工作完成时限
	 * @return
	 */
	public Date getCompleteDateLimit() {
		return completeDateLimit;
	}
	/**
	 * 设置工作完成时限
	 * @param completeDateLimit
	 */
	public void setCompleteDateLimit(Date completeDateLimit) {
		this.completeDateLimit = completeDateLimit;
	}
	/**
	 * 获取工作完成时限（字符串）
	 * @return
	 */
	public String getCompleteDateLimitStr() {
		return completeDateLimitStr;
	}
	/**
	 * 设置工作完成时限（字符串）
	 * @param completeDateLimitStr
	 */
	public void setCompleteDateLimitStr(String completeDateLimitStr) {
		this.completeDateLimitStr = completeDateLimitStr;
	}
	/**
	 * 获取主责人姓名
	 * @return
	 */
	public String getResponsibilityEmployeeName() {
		return responsibilityEmployeeName;
	}
	/**
	 * 设置主责人姓名
	 * @param responsibilityEmployeeName
	 */
	public void setResponsibilityEmployeeName(String responsibilityEmployeeName) {
		this.responsibilityEmployeeName = responsibilityEmployeeName;
	}
	/**
	 * 获取主责人所属组织名称
	 * @return
	 */
	public String getResponsibilityOrganizationName() {
		return responsibilityOrganizationName;
	}
	/**
	 * 设置主责人所属组织名称
	 * @param responsibilityOrganizationName
	 */
	public void setResponsibilityOrganizationName(String responsibilityOrganizationName) {
		this.responsibilityOrganizationName = responsibilityOrganizationName;
	}
	/**
	 * 获取主责人所属公司名称
	 * @return
	 */
	public String getResponsibilityCompanyName() {
		return responsibilityCompanyName;
	}
	/**
	 * 设置主责人所属公司名称
	 * @param responsibilityCompanyName
	 */
	public void setResponsibilityCompanyName(String responsibilityCompanyName) {
		this.responsibilityCompanyName = responsibilityCompanyName;
	}
	/**
	 * 获取协助人姓名（或能多值）
	 * @return
	 */
	public String getCooperateEmployeeName() {
		return cooperateEmployeeName;
	}
	/**
	 * 设置协助人姓名（或能多值）
	 * @param cooperateEmployeeName
	 */
	public void setCooperateEmployeeName(String cooperateEmployeeName) {
		this.cooperateEmployeeName = cooperateEmployeeName;
	}
	/**
	 * 获取协助人所属组织名称（或能多值）
	 * @return
	 */
	public String getCooperateOrganizationName() {
		return cooperateOrganizationName;
	}
	/**
	 * 设置协助人所属组织名称（或能多值）
	 * @param cooperateOrganizationName
	 */
	public void setCooperateOrganizationName(String cooperateOrganizationName) {
		this.cooperateOrganizationName = cooperateOrganizationName;
	}
	/**
	 * 获取协助人所属公司名称（或能多值）
	 * @return
	 */
	public String getCooperateCompanyName() {
		return cooperateCompanyName;
	}
	/**
	 * 设置协助人所属公司名称（或能多值）
	 * @param cooperateCompanyName
	 */
	public void setCooperateCompanyName(String cooperateCompanyName) {
		this.cooperateCompanyName = cooperateCompanyName;
	}
	/**
	 * 获取工作阅知领导名称（或能多值）
	 * @return
	 */
	public String getReadLeaderName() {
		return readLeaderName;
	}
	/**
	 * 设置工作阅知领导名称（或能多值）
	 * @param readLeaderName
	 */
	public void setReadLeaderName(String readLeaderName) {
		this.readLeaderName = readLeaderName;
	}
	/**
	 * 获取工作总体进度%
	 * @return
	 */
	public Double getOverallProgress() {
		return overallProgress;
	}
	/**
	 * 设置工作总体进度%
	 * @param overallProgress
	 */
	public void setOverallProgress(Double overallProgress) {
		this.overallProgress = overallProgress;
	}
	/**
	 * 获取工作处理状态：草稿|待确认|执行中|已超期|已完成|已撤消
	 * @return
	 */
	public String getWorkProcessStatus() {
		return workProcessStatus;
	}
	/**
	 * 设置工作处理状态：草稿|待确认|执行中|已超期|已完成|已撤消
	 * @param workProcessStatus
	 */
	public void setWorkProcessStatus(String workProcessStatus) {
		this.workProcessStatus = workProcessStatus;
	}
	/**
	 * 获取上一次汇报时间
	 * @return
	 */
	public Date getLastReportTime() {
		return lastReportTime;
	}
	/**
	 * 设置上一次汇报时间
	 * @param lastReportTime
	 */
	public void setLastReportTime(Date lastReportTime) {
		this.lastReportTime = lastReportTime;
	}
	/**
	 * 获取下一次汇报时间
	 * @return
	 */
	public Date getNextReportTime() {
		return nextReportTime;
	}
	/**
	 * 设置下一次汇报时间
	 * @param nextReportTime
	 */
	public void setNextReportTime(Date nextReportTime) {
		this.nextReportTime = nextReportTime;
	}
	/**
	 * 获取工作汇报次数
	 * @return
	 */
	public Integer getReportCount() {
		return reportCount;
	}
	/**
	 * 设置工作汇报次数
	 * @param reportCount
	 */
	public void setReportCount( Integer reportCount) {
		this.reportCount = reportCount;
	}
	/**
	 * 获取工作汇报时间队列，从工作部署日期到工作完成日期，根据汇报周期要求，分析出汇报时间队列以JSON方式存储
	 * @return
	 */
	public String getReportTimeQue() {
		return reportTimeQue;
	}
	/**
	 * 设置工作汇报时间队列，从工作部署日期到工作完成日期，根据汇报周期要求，分析出汇报时间队列以JSON方式存储
	 * @param reportTimeQue
	 */
	public void setReportTimeQue(String reportTimeQue) {
		this.reportTimeQue = reportTimeQue;
	}
	/**
	 * 获取汇报周期设定：每月汇报|每周汇报
	 * @return
	 */
	public String getReportCycle() {
		return reportCycle;
	}
	/**
	 * 设置汇报周期：每月汇报|每周汇报
	 * @param reportCycle
	 */
	public void setReportCycle(String reportCycle) {
		this.reportCycle = reportCycle;
	}
	/**
	 * 获取周期汇报日期设定：每月的几号，每周的星期几，启动时间由系统配置设定，比如：10:00
	 * @return
	 */
	public Integer getReportDayInCycle() {
		return reportDayInCycle;
	}
	/**
	 * 设置周期汇报日期：每月的几号，每周的星期几，启动时间由系统配置设定，比如：10:00
	 * @param reportDayInCycle
	 */
	public void setReportDayInCycle(Integer reportDayInCycle) {
		this.reportDayInCycle = reportDayInCycle;
	}
	/**
	 * 获取工作类别：工作类别由工作类别配置表决定
	 * @return
	 */
	public String getWorkType() {
		return workType;
	}
	/**
	 * 设置工作类别：工作类别由工作类别配置表决定
	 * @param workType
	 */
	public void setWorkType(String workType) {
		this.workType = workType;
	}
	/**
	 * 获取工作级别：工作级别由工作级别配置表决定
	 * @return
	 */
	public String getWorkLevel() {
		return workLevel;
	}
	/**
	 * 设置工作级别：工作级别由工作级别配置表决定
	 * @param workLevel
	 */
	public void setWorkLevel(String workLevel) {
		this.workLevel = workLevel;
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
	 * 获取工作是否已超期
	 * @return
	 */
	public Boolean getIsOverTime() {
		return isOverTime;
	}
	/**
	 * 设置工作是否已超期
	 * @param isOverTime
	 */
	public void setIsOverTime(Boolean isOverTime) {
		this.isOverTime = isOverTime;
	}
	/**
	 * 获取工作是否已完成
	 * @return
	 */
	public Boolean getIsCompleted() {
		return isCompleted;
	}
	/**
	 * 设置工作是否已完成
	 * @param isCompleted
	 */
	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}
	/**
	 * 获取工作期限类型:短期工作(不需要自动启动定期汇报) | 长期工作（需要自动启动定期汇报）
	 * @return
	 */
	public String getWorkDateTimeType() {
		return workDateTimeType;
	}
	/**
	 * 设置工作期限类型:短期工作(不需要自动启动定期汇报) | 长期工作（需要自动启动定期汇报）
	 * @param workDateTimeType
	 */
	public void setWorkDateTimeType(String workDateTimeType) {
		this.workDateTimeType = workDateTimeType;
	}
	/**
	 * 获取工作审核层级
	 * @return
	 */
	public Integer getWorkAuditLevel() {
		return workAuditLevel;
	}
	/**
	 * 工作审核层级
	 * @param workAuditLevel
	 */
	public void setWorkAuditLevel(Integer workAuditLevel) {
		this.workAuditLevel = workAuditLevel;
	}
	
	
	//==================================================================================================
	//=====================       其他方法    =============================================================
	//==================================================================================================
	/**
	 * 增加一个工作审核层级
	 */
	public void addWorkAuditLevel(){
		this.workAuditLevel++;
	}
	/**
	 * 减少一个工作审核层级
	 */
	public void minusWorkAuditLevel(){
		this.workAuditLevel--;
	}
	/**
	 * 增加一次已汇报次数
	 */
	public void addWorkReportCount(){
		this.reportCount++;
	}
	/**
	 * 减少一次已汇报次数
	 */
	public void minusaWorkReportCount(){
		this.reportCount--;
	}
	/**
	 * 获取是否需要汇报
	 * @return
	 */
	public Boolean getIsNeedReport() {
		return isNeedReport;
	}
	/**
	 * 设置是否需要汇报
	 * @param needReport
	 */
	public void setIsNeedReport( Boolean isNeedReport) {
		this.isNeedReport = isNeedReport;
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
	 * 获取阅知领导所属组织名称
	 * @return
	 */
	public String getReadLeaderOrganizationName() {
		return readLeaderOrganizationName;
	}
	/**
	 * 设置阅知领导所属组织名称
	 * @param readLeaderOrganizationName
	 */
	public void setReadLeaderOrganizationName(String readLeaderOrganizationName) {
		this.readLeaderOrganizationName = readLeaderOrganizationName;
	}
	/**
	 * 获取阅知领导所属公司名称
	 * @return
	 */
	public String getReadLeaderCompanyName() {
		return readLeaderCompanyName;
	}
	/**
	 * 设置阅知领导所属公司名称
	 * @param readLeaderCompanyName
	 */
	public void setReadLeaderCompanyName(String readLeaderCompanyName) {
		this.readLeaderCompanyName = readLeaderCompanyName;
	}
	/**
	 * 获取工作汇报是否需要管理员审核
	 * @return
	 */
	public Boolean getReportNeedAdminAudit() {
		return reportNeedAdminAudit;
	}
	/**
	 * 设置工作汇报是否需要管理员审核
	 * @param reportNeedAdminAudit
	 */
	public void setReportNeedAdminAudit(Boolean reportNeedAdminAudit) {
		this.reportNeedAdminAudit = reportNeedAdminAudit;
	}
	/**
	 * 获取工作管理员姓名
	 * @return
	 */
	public String getReportAdminName() {
		return reportAdminName;
	}
	/**
	 * 设置工作管理员姓名
	 * @param reportAdminName
	 */
	public void setReportAdminName(String reportAdminName) {
		this.reportAdminName = reportAdminName;
	}
	/**
	 * 获取附件列表
	 * @return
	 */
	public List<String> getAttachmentList() {
		return attachmentList;
	}
	/**
	 * 设置附件列表
	 * @param attachmentList
	 */
	public void setAttachmentList(List<String> attachmentList) {
		this.attachmentList = attachmentList;
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
	public String getResponsibilityIdentity() {
		return responsibilityIdentity;
	}
	public void setResponsibilityIdentity(String responsibilityIdentity) {
		this.responsibilityIdentity = responsibilityIdentity;
	}
	public String getCooperateIdentity() {
		return cooperateIdentity;
	}
	public void setCooperateIdentity(String cooperateIdentity) {
		this.cooperateIdentity = cooperateIdentity;
	}
	public String getReadLeaderIdentity() {
		return readLeaderIdentity;
	}
	public void setReadLeaderIdentity(String readLeaderIdentity) {
		this.readLeaderIdentity = readLeaderIdentity;
	}
	public String getReportAdminIdentity() {
		return reportAdminIdentity;
	}
	public void setReportAdminIdentity(String reportAdminIdentity) {
		this.reportAdminIdentity = reportAdminIdentity;
	}
	public String getShortWorkDetail() {
		return shortWorkDetail;
	}
	public void setShortWorkDetail(String shortWorkDetail) {
		this.shortWorkDetail = shortWorkDetail;
	}
	public String getShortDutyDescription() {
		return shortDutyDescription;
	}
	public void setShortDutyDescription(String shortDutyDescription) {
		this.shortDutyDescription = shortDutyDescription;
	}
	public String getShortProgressAction() {
		return shortProgressAction;
	}
	public void setShortProgressAction(String shortProgressAction) {
		this.shortProgressAction = shortProgressAction;
	}
	public String getShortLandmarkDescription() {
		return shortLandmarkDescription;
	}
	public void setShortLandmarkDescription(String shortLandmarkDescription) {
		this.shortLandmarkDescription = shortLandmarkDescription;
	}
	public String getShortResultDescription() {
		return shortResultDescription;
	}
	public void setShortResultDescription(String shortResultDescription) {
		this.shortResultDescription = shortResultDescription;
	}
	public String getShortMajorIssuesDescription() {
		return shortMajorIssuesDescription;
	}
	public void setShortMajorIssuesDescription(String shortMajorIssuesDescription) {
		this.shortMajorIssuesDescription = shortMajorIssuesDescription;
	}
	public String getShortProgressPlan() {
		return shortProgressPlan;
	}
	public void setShortProgressPlan(String shortProgressPlan) {
		this.shortProgressPlan = shortProgressPlan;
	}
	public String getProgressAnalyseTime() {
		return progressAnalyseTime;
	}
	public void setProgressAnalyseTime(String progressAnalyseTime) {
		this.progressAnalyseTime = progressAnalyseTime;
	}
	
}