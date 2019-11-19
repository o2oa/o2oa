package com.x.okr.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
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
 * 工作交流信息管理实体类
 * 
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrWorkChat.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.OkrWorkChat.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrWorkChat extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;

	private static final String TABLE = PersistenceProperties.OkrWorkChat.table;

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
	public static final String centerId_FIELDNAME = "centerId";
	@FieldDescribe("所属中心工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + centerId_FIELDNAME)
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

	public static final String senderName_FIELDNAME = "senderName";
	@FieldDescribe("发送者姓名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ senderName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String senderName = null;

	public static final String senderIdentity_FIELDNAME = "senderIdentity";
	@FieldDescribe("发送者身份")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ senderIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String senderIdentity = null;

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
	@CheckPersist(allowEmpty = true)
	private String targetIdentity = null;

	public static final String content_FIELDNAME = "content";
	@Lob
	@FieldDescribe("内容")
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + content_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String content = null;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("备注说明")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description = null;

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

	public String getTargetIdentity() {
		return targetIdentity;
	}

	public void setTargetIdentity(String targetIdentity) {
		this.targetIdentity = targetIdentity;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderIdentity() {
		return senderIdentity;
	}

	public void setSenderIdentity(String senderIdentity) {
		this.senderIdentity = senderIdentity;
	}

}