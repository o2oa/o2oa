package com.x.okr.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
 * 系统待办信息管理实体类
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrTaskHandled.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrTaskHandled extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrTaskHandled.table;

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
	
	@EntityFieldDescribe( "待办标题" )
	@Column(name="xtitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true)
	private String title = "";
	
	@EntityFieldDescribe( "办理类别: TASK|READ" )
	@Column(name="xprocessType", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true)
	private String processType = "TASK";
	
	@EntityFieldDescribe( "所属中心工作ID" )
	@Column( name="xcenterId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true)
	private String centerId = null;
	
	@EntityFieldDescribe( "中心工作标题" )
	@Column(name="xcenterTitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true)
	private String centerTitle = null;
	
	@EntityFieldDescribe( "所属工作ID" )
	@Column( name="xworkId", length = JpaObject.length_id )
	@Index(name = TABLE + "_workId" )
	@CheckPersist( allowEmpty = true)
	private String workId = null;
	
	@EntityFieldDescribe( "工作标题" )
	@Column(name="xworkTitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true)
	private String workTitle = null;
	
	@EntityFieldDescribe( "工作类别" )
	@Column(name="xworkType", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String workType = "";
	
	@EntityFieldDescribe( "动态对象类别:中心工作|工作|工作汇报|问题请示|交流" )
	@Column( name="xdynamicObjectType", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true)
	private String dynamicObjectType = null;
	
	@EntityFieldDescribe( "动态对象ID" )
	@Column( name="xdynamicObjectId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true)
	private String dynamicObjectId = null;
	
	@EntityFieldDescribe( "动态对象标题" )
	@Column(name="xdynamicObjectTitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true)
	private String dynamicObjectTitle = "";
	
	@EntityFieldDescribe( "到达时间" )
	@Column( name="xdateTimeStr", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true)
	private String arriveDateTimeStr = null;
	
	@EntityFieldDescribe( "到达时间" )
	@Column( name="xarriveDateTime" )
	@CheckPersist( allowEmpty = true)
	private Date arriveDateTime = null;
	
	@EntityFieldDescribe( "目标者姓名" )
	@Column(name="xtargetName", length = AbstractPersistenceProperties.organization_name_length )
	@CheckPersist( allowEmpty = true)
	private String targetName = null;
	
	@EntityFieldDescribe( "目标者身份" )
	@Column(name="xtargetIdentity", length = AbstractPersistenceProperties.organization_name_length )
	@CheckPersist( allowEmpty = true)
	private String targetIdentity = null;
	
	@EntityFieldDescribe( "目标者所属组织名称" )
	@Column(name="xtargetOrganizationName", length = AbstractPersistenceProperties.organization_name_length )
	@CheckPersist( allowEmpty = true)
	private String targetOrganizationName = null;
	
	@EntityFieldDescribe( "目标者所属公司名称" )
	@Column(name="xtargetCompanyName", length = AbstractPersistenceProperties.organization_name_length )
	@CheckPersist( allowEmpty = true)
	private String targetCompanyName = null;
	
	@EntityFieldDescribe( "办理时间" )
	@Column( name="xprocessDateTimeStr", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true)
	private String processDateTimeStr = null;
	
	@EntityFieldDescribe( "办理时间" )
	@Column( name="xprocessDateTime" )
	@CheckPersist( allowEmpty = true)
	private Date processDateTime = null;
	
	@EntityFieldDescribe( "处理环节名称" )
	@Column(name="xactivityName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String activityName = "";
	
	@EntityFieldDescribe( "访问链接" )
	@Column(name="xviewUrl", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String viewUrl = "";
	
	@EntityFieldDescribe( "办理时长" )
	@Column(name="xduration" )
	@CheckPersist( allowEmpty = true )
	private Long duration = 0L;
	
	@EntityFieldDescribe( "处理状态：正常|已删除" )
	@Column(name="xstatus", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = true )
	private String status = "正常";
	
	/**
	 * 获取动态对象类别:中心工作|工作|工作汇报|问题请示|交流
	 * @return
	 */
	public String getDynamicObjectType() {
		return dynamicObjectType;
	}
	/**
	 * 设置动态对象类别:中心工作|工作|工作汇报|问题请示|交流
	 * @param dynamicObjectType
	 */
	public void setDynamicObjectType(String dynamicObjectType) {
		this.dynamicObjectType = dynamicObjectType;
	}
	/**
	 * 获取动态对象ID
	 * @return
	 */
	public String getDynamicObjectId() {
		return dynamicObjectId;
	}
	/**
	 * 设置动态对象ID
	 * @param dynamicObjectId
	 */
	public void setDynamicObjectId(String dynamicObjectId) {
		this.dynamicObjectId = dynamicObjectId;
	}
	/**
	 * 获取动态对象标题
	 * @return
	 */
	public String getDynamicObjectTitle() {
		return dynamicObjectTitle;
	}
	/**
	 * 设置动态对象标题
	 * @param dynamicObjectTitle
	 */
	public void setDynamicObjectTitle(String dynamicObjectTitle) {
		this.dynamicObjectTitle = dynamicObjectTitle;
	}
	/**
	 * 获取目标对象名称
	 * @return
	 */
	public String getTargetName() {
		return targetName;
	}
	/**
	 * 设置目标对象名称
	 * @param targetName
	 */
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
	/**
	 * 获取目标访问URL
	 * @return
	 */
	public String getViewUrl() {
		return viewUrl;
	}
	/**
	 * 设置目标访问URL
	 * @param viewUrl
	 */
	public void setViewUrl(String viewUrl) {
		this.viewUrl = viewUrl;
	}
	/**
	 * 获取待办标题
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * 设置待办标题
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 获取中心工作ID
	 * @return
	 */
	public String getCenterId() {
		return centerId;
	}
	/**
	 * 设置中心工作ID
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
	 * 获取工作ID
	 * @return
	 */
	public String getWorkId() {
		return workId;
	}
	/**
	 * 设置工作ID
	 * @param workId
	 */
	public void setWorkId(String workId) {
		this.workId = workId;
	}
	/**
	 * 获取工作标题
	 * @return
	 */
	public String getWorkTitle() {
		return workTitle;
	}
	/**
	 * 设置工作标题
	 * @param workTitle
	 */
	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}
	/**
	 * 获取待办到达时间
	 * @return
	 */
	public String getArriveDateTimeStr() {
		return arriveDateTimeStr;
	}
	/**
	 * 设置待办到达时间
	 * @param arriveDateTimeStr
	 */
	public void setArriveDateTimeStr(String arriveDateTimeStr) {
		this.arriveDateTimeStr = arriveDateTimeStr;
	}
	/**
	 * 获取待办到达时间
	 * @return
	 */
	public Date getArriveDateTime() {
		return arriveDateTime;
	}
	/**
	 * 设置待办到达时间
	 * @param arriveDateTime
	 */
	public void setArriveDateTime(Date arriveDateTime) {
		this.arriveDateTime = arriveDateTime;
	}
	/**
	 * 获取办理人所属组织名称
	 * @return
	 */
	public String getTargetOrganizationName() {
		return targetOrganizationName;
	}
	/**
	 * 设置办理人所属组织名称
	 * @param targetOrganizationName
	 */
	public void setTargetOrganizationName(String targetOrganizationName) {
		this.targetOrganizationName = targetOrganizationName;
	}
	/**
	 * 获取办理人所属公司名称
	 * @return
	 */
	public String getTargetCompanyName() {
		return targetCompanyName;
	}
	/**
	 * 设置办理人所属公司名称
	 * @param targetCompanyName
	 */
	public void setTargetCompanyName(String targetCompanyName) {
		this.targetCompanyName = targetCompanyName;
	}
	/**
	 * 获取办理环节名称
	 * @return
	 */
	public String getActivityName() {
		return activityName;
	}
	/**
	 * 设置办理环节名称
	 * @param activityName
	 */
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	/**
	 * 获取办理时间
	 * @return
	 */
	public Date getProcessDateTime() {
		return processDateTime;
	}
	/**
	 * 设置办理时间
	 * @param processDateTime
	 */
	public void setProcessDateTime(Date processDateTime) {
		this.processDateTime = processDateTime;
	}
	/**
	 * 获取办理时间
	 * @return
	 */
	public String getProcessDateTimeStr() {
		return processDateTimeStr;
	}
	/**
	 * 设置办理时间
	 * @param processDateTimeStr
	 */
	public void setProcessDateTimeStr(String processDateTimeStr) {
		this.processDateTimeStr = processDateTimeStr;
	}
	/**
	 * 获取办理耗时
	 * @return
	 */
	public Long getDuration() {
		return duration;
	}
	/**
	 * 设置办理耗时
	 * @param duration
	 */
	public void setDuration(Long duration) {
		this.duration = duration;
	}
	/**
	 * 获取办理类别：TASK|READ
	 * @return
	 */
	public String getProcessType() {
		return processType;
	}
	/**
	 * 设置办理类别：TASK|READ
	 * @param processType
	 */
	public void setProcessType(String processType) {
		this.processType = processType;
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
	 * 获取目标用户身份
	 * @return
	 */
	public String getTargetIdentity() {
		return targetIdentity;
	}
	/**
	 * 设置目标用户身份
	 * @param targetIdentity
	 */
	public void setTargetIdentity(String targetIdentity) {
		this.targetIdentity = targetIdentity;
	}
	public String getWorkType() {
		return workType;
	}
	public void setWorkType(String workType) {
		this.workType = workType;
	}
	
}