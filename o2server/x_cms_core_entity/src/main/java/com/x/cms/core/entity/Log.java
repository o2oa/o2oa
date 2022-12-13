package com.x.cms.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 内容管理日志信息表
 *
 */
@Schema(name = "Log", description = "内容管理日志信息.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.log, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Log.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Log.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Log extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.Log.table;

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
	public static final String operationType_FIELDNAME = "operationType";
	@FieldDescribe("操作类别：新增|修改|删除|查看|查询")
	@Index(name = TABLE + IndexNameMiddle + operationType_FIELDNAME)
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + operationType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String operationType;

	public static final String operationLevel_FIELDNAME = "operationLevel";
	@FieldDescribe("操作级别：应用|分类|文档|文件")
	@Index(name = TABLE + IndexNameMiddle + operationLevel_FIELDNAME)
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + operationLevel_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String operationLevel;

	public static final String appId_FIELDNAME = "appId";
	@FieldDescribe("操作对象：应用ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + appId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + appId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appId;

	public static final String categoryId_FIELDNAME = "categoryId";
	@FieldDescribe("操作对象：分类ID")
	@Index(name = TABLE + IndexNameMiddle + categoryId_FIELDNAME)
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + categoryId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categoryId;

	public static final String documentId_FIELDNAME = "documentId";
	@FieldDescribe("操作对象：文档ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + documentId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String documentId;

	public static final String fileId_FIELDNAME = "fileId";
	@FieldDescribe("操作对象：文件ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + fileId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fileId;

	public static final String operatorUid_FIELDNAME = "operatorUid";
	@FieldDescribe("操作者UID")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + operatorUid_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + operatorUid_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String operatorUid;

	public static final String operatorName_FIELDNAME = "operatorName";
	@FieldDescribe("操作者姓名")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + operatorName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String operatorName;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("操作文字描述")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	/**
	 * 获取操作类别
	 * 
	 * @return
	 */
	public String getOperationType() {
		return operationType;
	}

	/**
	 * 设置操作类别
	 * 
	 * @param operationType
	 */
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	/**
	 * 获取操作者帐号
	 * 
	 * @return
	 */
	public String getOperatorUid() {
		return operatorUid;
	}

	/**
	 * 设置操作者帐号
	 * 
	 * @param operatorUid
	 */
	public void setOperatorUid(String operatorUid) {
		this.operatorUid = operatorUid;
	}

	/**
	 * 获取操作者姓名
	 * 
	 * @return
	 */
	public String getOperatorName() {
		return operatorName;
	}

	/**
	 * 设置操作者姓名
	 * 
	 * @param operatorName
	 */
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	/**
	 * 获取操作日志文字描述
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置操作日志文字描述
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取操作的应用ID
	 * 
	 * @return
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * 设置操作的应用ID
	 * 
	 * @param appId
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * 获取操作的分类ID
	 * 
	 * @return
	 */
	public String getCategoryId() {
		return categoryId;
	}

	/**
	 * 设置操作的分类ID
	 * 
	 * @param appId
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * 获取操作的文档ID
	 * 
	 * @return
	 */
	public String getDocumentId() {
		return documentId;
	}

	/**
	 * 设置操作的文档ID
	 * 
	 * @param appId
	 */
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	/**
	 * 获取操作的文件ID
	 * 
	 * @return
	 */
	public String getFileId() {
		return fileId;
	}

	/**
	 * 设置操作的文件ID
	 * 
	 * @param appId
	 */
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	/**
	 * 操作对象级别：应用|分类|文档|文件
	 * 
	 * @return
	 */
	public String getOperationLevel() {
		return operationLevel;
	}

	/**
	 * 操作对象级别：应用|分类|文档|文件
	 * 
	 * @return
	 */
	public void setOperationLevel(String operationLevel) {
		this.operationLevel = operationLevel;
	}
}