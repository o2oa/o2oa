package com.x.okr.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
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
@Table(name = PersistenceProperties.OkrTask.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.OkrTask.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrTask extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrTask.table;

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

	@FieldDescribe("待办标题")
	@Column(name = "xtitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String title = "";

	@FieldDescribe("办理类别: TASK|READ")
	@Column(name = "xprocessType", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String processType = "TASK";

	@FieldDescribe("所属中心工作ID")
	@Column(name = "xcenterId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String centerId = null;

	@FieldDescribe("中心工作标题")
	@Column(name = "xcenterTitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String centerTitle = null;

	@FieldDescribe("所属工作ID")
	@Column(name = "xworkId", length = JpaObject.length_id)
	@Index(name = TABLE + "_workId")
	@CheckPersist(allowEmpty = true)
	private String workId = null;

	@FieldDescribe("工作标题")
	@Column(name = "xworkTitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String workTitle = null;

	@FieldDescribe("工作类别")
	@Column(name = "xworkType", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String workType = "";

	@FieldDescribe("动态对象类别:中心工作|工作|工作汇报|问题请示|交流")
	@Column(name = "xdynamicObjectType", length = JpaObject.length_32B)
	@Index(name = TABLE + "_dynamicObjectType")
	@CheckPersist(allowEmpty = true)
	private String dynamicObjectType = null;

	@FieldDescribe("动态对象ID")
	@Column(name = "xdynamicObjectId", length = JpaObject.length_id)
	@Index(name = TABLE + "_dynamicObjectId")
	@CheckPersist(allowEmpty = true)
	private String dynamicObjectId = null;

	@FieldDescribe("动态对象标题")
	@Column(name = "xdynamicObjectTitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String dynamicObjectTitle = "";

	public static final String arriveDateTimeStr_FIELDNAME = "arriveDateTimeStr";
	@FieldDescribe("到达时间")
	@Column(name = ColumnNamePrefix + arriveDateTimeStr_FIELDNAME, length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String arriveDateTimeStr = null;

	@FieldDescribe("到达时间")
	@Column(name = "xarriveDateTime")
	@CheckPersist(allowEmpty = true)
	private Date arriveDateTime = null;

	@FieldDescribe("目标者姓名")
	@Column(name = "xtargetName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String targetName = null;

	@FieldDescribe("目标者身份")
	@Column(name = "xtargetIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_targetIdentity")
	@CheckPersist(allowEmpty = true)
	private String targetIdentity = null;

	@FieldDescribe("目标者所属组织名称")
	@Column(name = "xtargetUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String targetUnitName = null;

	@FieldDescribe("目标者所属顶层组织名称")
	@Column(name = "xtargetTopUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String targetTopUnitName = null;

	@FieldDescribe("处理环节名称")
	@Column(name = "xactivityName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String activityName = "";

	@FieldDescribe("访问链接")
	@Column(name = "xviewUrl", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String viewUrl = "";

	@FieldDescribe("处理状态：正常|已删除")
	@Column(name = "xstatus", length = JpaObject.length_16B)
	@Index(name = TABLE + "_status")
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