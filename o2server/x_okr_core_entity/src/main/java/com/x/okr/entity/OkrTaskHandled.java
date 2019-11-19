package com.x.okr.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 系统待办信息管理实体类
 * 
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrTaskHandled.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.OkrTaskHandled.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrTaskHandled extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrTaskHandled.table;

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
	@FieldDescribe("待办标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title = "";

	public static final String processType_FIELDNAME = "processType";
	@FieldDescribe("办理类别: TASK|READ")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + processType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + processType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processType = "TASK";

	public static final String centerId_FIELDNAME = "centerId";
	@FieldDescribe("所属中心工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + centerId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + centerId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String centerId = null;

	public static final String centerTitle_FIELDNAME = "centerTitle";
	@FieldDescribe("中心工作标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + centerTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String centerTitle = null;

	public static final String workId_FIELDNAME = "workId";
	@FieldDescribe("所属工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + workId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workId = null;

	public static final String workTitle_FIELDNAME = "workTitle";
	@FieldDescribe("工作标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + workTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workTitle = null;

	public static final String workType_FIELDNAME = "workType";
	@FieldDescribe("工作类别")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + workType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workType = "";

	public static final String dynamicObjectType_FIELDNAME = "dynamicObjectType";
	@FieldDescribe("动态对象类别:中心工作|工作|工作汇报|问题请示|交流")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + dynamicObjectType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String dynamicObjectType = null;

	public static final String dynamicObjectId_FIELDNAME = "dynamicObjectId";
	@FieldDescribe("动态对象ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + dynamicObjectId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String dynamicObjectId = null;

	public static final String dynamicObjectTitle_FIELDNAME = "dynamicObjectTitle";
	@FieldDescribe("动态对象标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + dynamicObjectTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String dynamicObjectTitle = "";

	public static final String arriveDateTimeStr_FIELDNAME = "arriveDateTimeStr";
	@FieldDescribe("到达时间")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + arriveDateTimeStr_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String arriveDateTimeStr = null;

	public static final String arriveDateTime_FIELDNAME = "arriveDateTime";
	@FieldDescribe("到达时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + arriveDateTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date arriveDateTime = null;

	public static final String targetName_FIELDNAME = "targetName";
	@FieldDescribe("目标者姓名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ targetName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + targetName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String targetName = null;

	public static final String targetIdentity_FIELDNAME = "targetIdentity";
	@FieldDescribe("目标者身份")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ targetIdentity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + targetIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String targetIdentity = null;

	public static final String targetUnitName_FIELDNAME = "targetUnitName";
	@FieldDescribe("目标者所属组织名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ targetUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String targetUnitName = null;

	public static final String targetTopUnitName_FIELDNAME = "targetTopUnitName";
	@FieldDescribe("目标者所属顶层组织名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ targetTopUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String targetTopUnitName = null;

	public static final String processDateTimeStr_FIELDNAME = "processDateTimeStr";
	@FieldDescribe("办理时间字符串")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + processDateTimeStr_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processDateTimeStr = null;

	public static final String processDateTime_FIELDNAME = "processDateTime";
	@FieldDescribe("办理时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + processDateTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date processDateTime = null;

	public static final String activityName_FIELDNAME = "activityName";
	@FieldDescribe("处理环节名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ activityName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityName = "";

	public static final String viewUrl_FIELDNAME = "viewUrl";
	@FieldDescribe("访问链接")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + viewUrl_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String viewUrl = "";

	public static final String duration_FIELDNAME = "duration";
	@FieldDescribe("办理时长")
	@Column(name = ColumnNamePrefix + duration_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long duration = 0L;

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("处理状态：正常|已删除")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + status_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String status = "正常";

	/**
	 * 获取动态对象类别:中心工作|工作|工作汇报|问题请示|交流
	 * 
	 * @return
	 */
	public String getDynamicObjectType() {
		return dynamicObjectType;
	}

	/**
	 * 设置动态对象类别:中心工作|工作|工作汇报|问题请示|交流
	 * 
	 * @param dynamicObjectType
	 */
	public void setDynamicObjectType(String dynamicObjectType) {
		this.dynamicObjectType = dynamicObjectType;
	}

	/**
	 * 获取动态对象ID
	 * 
	 * @return
	 */
	public String getDynamicObjectId() {
		return dynamicObjectId;
	}

	/**
	 * 设置动态对象ID
	 * 
	 * @param dynamicObjectId
	 */
	public void setDynamicObjectId(String dynamicObjectId) {
		this.dynamicObjectId = dynamicObjectId;
	}

	/**
	 * 获取动态对象标题
	 * 
	 * @return
	 */
	public String getDynamicObjectTitle() {
		return dynamicObjectTitle;
	}

	/**
	 * 设置动态对象标题
	 * 
	 * @param dynamicObjectTitle
	 */
	public void setDynamicObjectTitle(String dynamicObjectTitle) {
		this.dynamicObjectTitle = dynamicObjectTitle;
	}

	/**
	 * 获取目标对象名称
	 * 
	 * @return
	 */
	public String getTargetName() {
		return targetName;
	}

	/**
	 * 设置目标对象名称
	 * 
	 * @param targetName
	 */
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	/**
	 * 获取目标访问URL
	 * 
	 * @return
	 */
	public String getViewUrl() {
		return viewUrl;
	}

	/**
	 * 设置目标访问URL
	 * 
	 * @param viewUrl
	 */
	public void setViewUrl(String viewUrl) {
		this.viewUrl = viewUrl;
	}

	/**
	 * 获取待办标题
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 设置待办标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 获取中心工作ID
	 * 
	 * @return
	 */
	public String getCenterId() {
		return centerId;
	}

	/**
	 * 设置中心工作ID
	 * 
	 * @param centerId
	 */
	public void setCenterId(String centerId) {
		this.centerId = centerId;
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
	 * 获取工作ID
	 * 
	 * @return
	 */
	public String getWorkId() {
		return workId;
	}

	/**
	 * 设置工作ID
	 * 
	 * @param workId
	 */
	public void setWorkId(String workId) {
		this.workId = workId;
	}

	/**
	 * 获取工作标题
	 * 
	 * @return
	 */
	public String getWorkTitle() {
		return workTitle;
	}

	/**
	 * 设置工作标题
	 * 
	 * @param workTitle
	 */
	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	/**
	 * 获取待办到达时间
	 * 
	 * @return
	 */
	public String getArriveDateTimeStr() {
		return arriveDateTimeStr;
	}

	/**
	 * 设置待办到达时间
	 * 
	 * @param arriveDateTimeStr
	 */
	public void setArriveDateTimeStr(String arriveDateTimeStr) {
		this.arriveDateTimeStr = arriveDateTimeStr;
	}

	/**
	 * 获取待办到达时间
	 * 
	 * @return
	 */
	public Date getArriveDateTime() {
		return arriveDateTime;
	}

	/**
	 * 设置待办到达时间
	 * 
	 * @param arriveDateTime
	 */
	public void setArriveDateTime(Date arriveDateTime) {
		this.arriveDateTime = arriveDateTime;
	}

	/**
	 * 获取办理人所属组织名称
	 * 
	 * @return
	 */
	public String getTargetUnitName() {
		return targetUnitName;
	}

	/**
	 * 设置办理人所属组织名称
	 * 
	 * @param targetUnitName
	 */
	public void setTargetUnitName(String targetUnitName) {
		this.targetUnitName = targetUnitName;
	}

	/**
	 * 获取办理人所属顶层组织名称
	 * 
	 * @return
	 */
	public String getTargetTopUnitName() {
		return targetTopUnitName;
	}

	/**
	 * 设置办理人所属顶层组织名称
	 * 
	 * @param targetTopUnitName
	 */
	public void setTargetTopUnitName(String targetTopUnitName) {
		this.targetTopUnitName = targetTopUnitName;
	}

	/**
	 * 获取办理环节名称
	 * 
	 * @return
	 */
	public String getActivityName() {
		return activityName;
	}

	/**
	 * 设置办理环节名称
	 * 
	 * @param activityName
	 */
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	/**
	 * 获取办理时间
	 * 
	 * @return
	 */
	public Date getProcessDateTime() {
		return processDateTime;
	}

	/**
	 * 设置办理时间
	 * 
	 * @param processDateTime
	 */
	public void setProcessDateTime(Date processDateTime) {
		this.processDateTime = processDateTime;
	}

	/**
	 * 获取办理时间
	 * 
	 * @return
	 */
	public String getProcessDateTimeStr() {
		return processDateTimeStr;
	}

	/**
	 * 设置办理时间
	 * 
	 * @param processDateTimeStr
	 */
	public void setProcessDateTimeStr(String processDateTimeStr) {
		this.processDateTimeStr = processDateTimeStr;
	}

	/**
	 * 获取办理耗时
	 * 
	 * @return
	 */
	public Long getDuration() {
		return duration;
	}

	/**
	 * 设置办理耗时
	 * 
	 * @param duration
	 */
	public void setDuration(Long duration) {
		this.duration = duration;
	}

	/**
	 * 获取办理类别：TASK|READ
	 * 
	 * @return
	 */
	public String getProcessType() {
		return processType;
	}

	/**
	 * 设置办理类别：TASK|READ
	 * 
	 * @param processType
	 */
	public void setProcessType(String processType) {
		this.processType = processType;
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
	 * 获取目标用户身份
	 * 
	 * @return
	 */
	public String getTargetIdentity() {
		return targetIdentity;
	}

	/**
	 * 设置目标用户身份
	 * 
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