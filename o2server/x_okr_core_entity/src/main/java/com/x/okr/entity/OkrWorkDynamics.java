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
 * 工作动态信息管理实体类
 * 
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrWorkDynamics.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.OkrWorkDynamics.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrWorkDynamics extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrWorkDynamics.table;

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
	public static final String dynamicType_FIELDNAME = "dynamicType";
	@FieldDescribe("工作动态类别：工作部署|工作确认|工作拆解|创建工作汇报|提交工作汇报|审阅工作汇报|创建问题请示|提交问题请示|审阅问题请示|交流")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + dynamicType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String dynamicType = "未知类别";

	public static final String dynamicObjectType_FIELDNAME = "dynamicObjectType";
	@FieldDescribe("动态对象类别:中心工作|工作|工作汇报|问题请示|交流")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + dynamicObjectType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dynamicObjectType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String dynamicObjectType = "未知类别";

	public static final String dynamicObjectId_FIELDNAME = "dynamicObjectId";
	@FieldDescribe("动态对象ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + dynamicObjectId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dynamicObjectId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String dynamicObjectId = null;

	public static final String dynamicObjectTitle_FIELDNAME = "dynamicObjectTitle";
	@FieldDescribe("动态对象标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + dynamicObjectTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String dynamicObjectTitle = "";

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

	public static final String dateTimeStr_FIELDNAME = "dateTimeStr";
	@FieldDescribe("操作时间")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + dateTimeStr_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String dateTimeStr = null;

	public static final String dateTime_FIELDNAME = "dateTime";
	@FieldDescribe("操作时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + dateTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateTime = null;

	public static final String operatorName_FIELDNAME = "operatorName";
	@FieldDescribe("操作者姓名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ operatorName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + operatorName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String operatorName = null;

	public static final String targetName_FIELDNAME = "targetName";
	@FieldDescribe("目标者姓名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ targetName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String targetName = null;

	public static final String targetIdentity_FIELDNAME = "targetIdentity";
	@FieldDescribe("目标者身份")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ targetIdentity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + targetIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String targetIdentity = null;

	public static final String content_FIELDNAME = "content";
	@FieldDescribe("内容")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + content_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String content = null;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("备注说明")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description = null;

	public static final String viewUrl_FIELDNAME = "viewUrl";
	@FieldDescribe("访问链接")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + viewUrl_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String viewUrl = null;

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("处理状态：正常|已删除")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + status_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + status_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String status = "正常";

	/**
	 * 获取工作动态类别：工作部署|工作确认|工作拆解|创建工作汇报|提交工作汇报|审阅工作汇报|创建问题请示|提交问题请示|审阅问题请示|交流
	 * 
	 * @return
	 */
	public String getDynamicType() {
		return dynamicType;
	}

	/**
	 * 设置工作动态类别：工作部署|工作确认|工作拆解|创建工作汇报|提交工作汇报|审阅工作汇报|创建问题请示|提交问题请示|审阅问题请示|交流
	 * 
	 * @param dynamicType
	 */
	public void setDynamicType(String dynamicType) {
		this.dynamicType = dynamicType;
	}

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
	 * 获取操作时间
	 * 
	 * @return
	 */
	public String getDateTimeStr() {
		return dateTimeStr;
	}

	/**
	 * 设置操作时间
	 * 
	 * @param dateTimeStr
	 */
	public void setDateTimeStr(String dateTimeStr) {
		this.dateTimeStr = dateTimeStr;
	}

	/**
	 * 获取操作时间
	 * 
	 * @return
	 */
	public Date getDateTime() {
		return dateTime;
	}

	/**
	 * 设置操作时间
	 * 
	 * @param dateTime
	 */
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * 获取操作对象名称
	 * 
	 * @return
	 */
	public String getOperatorName() {
		return operatorName;
	}

	/**
	 * 设置操作对象名称
	 * 
	 * @param operatorName
	 */
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
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
	 * 获取操作描述
	 * 
	 * @return
	 */
	public String getContent() {
		return content;
	}

	/**
	 * 设置操作描述
	 * 
	 * @param content
	 */
	public void setContent(String content) {
		this.content = content;
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
	 * 获取备注说明信息
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置备注说明信息
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
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

	public String getTargetIdentity() {
		return targetIdentity;
	}

	public void setTargetIdentity(String targetIdentity) {
		this.targetIdentity = targetIdentity;
	}

}