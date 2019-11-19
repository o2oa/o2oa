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
 * 工作委托记录信息管理实体类
 * 
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrWorkAuthorizeRecord.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.OkrWorkAuthorizeRecord.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrWorkAuthorizeRecord extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrWorkAuthorizeRecord.table;

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
	public static final String workId_FIELDNAME = "workId";
	@FieldDescribe("工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + workId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workId = "";

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("工作标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title = "";

	public static final String centerId_FIELDNAME = "centerId";
	@FieldDescribe("中心工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + centerId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String centerId = "";

	public static final String centerTitle_FIELDNAME = "centerTitle";
	@FieldDescribe("中心工作标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + centerTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String centerTitle = "";

	public static final String delegatorName_FIELDNAME = "delegatorName";
	@FieldDescribe("委托者姓名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ delegatorName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String delegatorName = "";

	public static final String delegatorIdentity_FIELDNAME = "delegatorIdentity";
	@FieldDescribe("委托者身份")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ delegatorIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String delegatorIdentity = "";

	public static final String delegatorUnitName_FIELDNAME = "delegatorUnitName";
	@FieldDescribe("委托者所属组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ delegatorUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String delegatorUnitName = "";

	public static final String delegatorTopUnitName_FIELDNAME = "delegatorTopUnitName";
	@FieldDescribe("委托者所属顶层组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ delegatorTopUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String delegatorTopUnitName = "";

	public static final String delegateLevel_FIELDNAME = "delegateLevel";
	@FieldDescribe("委托层级")
	@Column(name = ColumnNamePrefix + delegateLevel_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer delegateLevel = 1;

	public static final String targetName_FIELDNAME = "targetName";
	@FieldDescribe("受托者姓名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ targetName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String targetName = "";

	public static final String targetIdentity_FIELDNAME = "targetIdentity";
	@FieldDescribe("受托者身份")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ targetIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String targetIdentity = null;

	public static final String targetUnitName_FIELDNAME = "targetUnitName";
	@FieldDescribe("受托者所属组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ targetUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String targetUnitName = "";

	public static final String targetTopUnitName_FIELDNAME = "targetTopUnitName";
	@FieldDescribe("受托者所属顶层组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ targetTopUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String targetTopUnitName = "";

	public static final String delegateDateTimeStr_FIELDNAME = "delegateDateTimeStr";
	@FieldDescribe("委托时间：yyyy-mm-dd hh:mi:ss")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + delegateDateTimeStr_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String delegateDateTimeStr = "";

	public static final String delegateDateTime_FIELDNAME = "delegateDateTime";
	@FieldDescribe("委托时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + delegateDateTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date delegateDateTime = null;

	public static final String takebackDateTime_FIELDNAME = "takebackDateTime";
	@FieldDescribe("收回时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + takebackDateTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date takebackDateTime = null;

	public static final String delegateOpinion_FIELDNAME = "delegateOpinion";
	@FieldDescribe("委托意见")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + delegateOpinion_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String delegateOpinion = "";

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("信息状态：正常|已删除")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + status_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + status_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String status = "正常";

	/**
	 * 获取工作标题
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 设置工作标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
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
	 * @param parentWorkId
	 */
	public void setWorkId(String workId) {
		this.workId = workId;
	}

	/**
	 * 获取委托者姓名
	 * 
	 * @return
	 */
	public String getDelegatorName() {
		return delegatorName;
	}

	/**
	 * 设置委托者姓名
	 * 
	 * @param delegatorName
	 */
	public void setDelegatorName(String delegatorName) {
		this.delegatorName = delegatorName;
	}

	/**
	 * 获取委托者所属组织名称
	 * 
	 * @return
	 */
	public String getDelegatorUnitName() {
		return delegatorUnitName;
	}

	/**
	 * 设置委托者所属组织名称
	 * 
	 * @param delegatorUnitName
	 */
	public void setDelegatorUnitName(String delegatorUnitName) {
		this.delegatorUnitName = delegatorUnitName;
	}

	/**
	 * 获取委托者所属顶层组织名称
	 * 
	 * @return
	 */
	public String getDelegatorTopUnitName() {
		return delegatorTopUnitName;
	}

	/**
	 * 设置委托者所属顶层组织名称
	 * 
	 * @param delegatorTopUnitName
	 */
	public void setDelegatorTopUnitName(String delegatorTopUnitName) {
		this.delegatorTopUnitName = delegatorTopUnitName;
	}

	/**
	 * 获取受托者姓名
	 * 
	 * @return
	 */
	public String getTargetName() {
		return targetName;
	}

	/**
	 * 设置受托者姓名
	 * 
	 * @param targetName
	 */
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	/**
	 * 获取受托者所属组织名称
	 * 
	 * @return
	 */
	public String getTargetUnitName() {
		return targetUnitName;
	}

	/**
	 * 设置受托者所属组织名称
	 * 
	 * @param targetUnitName
	 */
	public void setTargetUnitName(String targetUnitName) {
		this.targetUnitName = targetUnitName;
	}

	/**
	 * 获取受托者所属顶层组织名称
	 * 
	 * @return
	 */
	public String getTargetTopUnitName() {
		return targetTopUnitName;
	}

	/**
	 * 设置受托者所属顶层组织名称
	 * 
	 * @param targetTopUnitName
	 */
	public void setTargetTopUnitName(String targetTopUnitName) {
		this.targetTopUnitName = targetTopUnitName;
	}

	/**
	 * 获取委托时间
	 * 
	 * @return
	 */
	public String getDelegateDateTimeStr() {
		return delegateDateTimeStr;
	}

	/**
	 * 设置委托时间
	 * 
	 * @param delegateDateTimeStr
	 */
	public void setDelegateDateTimeStr(String delegateDateTimeStr) {
		this.delegateDateTimeStr = delegateDateTimeStr;
	}

	/**
	 * 获取委托时间
	 * 
	 * @return
	 */
	public Date getDelegateDateTime() {
		return delegateDateTime;
	}

	/**
	 * 设置委托时间
	 * 
	 * @param delegateDateTime
	 */
	public void setDelegateDateTime(Date delegateDateTime) {
		this.delegateDateTime = delegateDateTime;
	}

	/**
	 * 获取委托意见
	 * 
	 * @return
	 */
	public String getDelegateOpinion() {
		return delegateOpinion;
	}

	/**
	 * 设置委托意见
	 * 
	 * @param delegateOpinion
	 */
	public void setDelegateOpinion(String delegateOpinion) {
		this.delegateOpinion = delegateOpinion;
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
	 * 获取委托者身份
	 * 
	 * @return
	 */
	public String getDelegatorIdentity() {
		return delegatorIdentity;
	}

	/**
	 * 设置委托者身份
	 * 
	 * @param delegatorIdentity
	 */
	public void setDelegatorIdentity(String delegatorIdentity) {
		this.delegatorIdentity = delegatorIdentity;
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

	public Integer getDelegateLevel() {
		return delegateLevel;
	}

	public void setDelegateLevel(Integer delegateLevel) {
		this.delegateLevel = delegateLevel;
	}

	public Date getTakebackDateTime() {
		return takebackDateTime;
	}

	public void setTakebackDateTime(Date takebackDateTime) {
		this.takebackDateTime = takebackDateTime;
	}
}