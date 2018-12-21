package com.x.cms.core.entity;

import java.util.Date;

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

/**
 * 文档可见权限记录表 (取消数据结构，后续删除)
 * 
 * @author 李义
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.DocumentPermission.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.DocumentPermission.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class DocumentPermission extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.DocumentPermission.table;

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
	public static final String documentId_FIELDNAME = "documentId";
	@FieldDescribe("文档ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + documentId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String documentId;

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("文档标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title = "无标题";

	public static final String appId_FIELDNAME = "appId";
	@FieldDescribe("应用ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + appId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + appId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appId;

	public static final String appName_FIELDNAME = "appName";
	@FieldDescribe("应用名称")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + appName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appName;

	public static final String categoryId_FIELDNAME = "categoryId";
	@FieldDescribe("分类ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + categoryId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + categoryId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categoryId;

	public static final String categoryName_FIELDNAME = "categoryName";
	@FieldDescribe("分类名称")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + categoryName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categoryName;

	public static final String categoryAlias_FIELDNAME = "categoryAlias";
	@FieldDescribe("分类别名")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + categoryAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categoryAlias;

	public static final String docCreateDate_FIELDNAME = "docCreateDate";
	@FieldDescribe("创建日期")
	@Column( name = ColumnNamePrefix + docCreateDate_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + docCreateDate_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date docCreateDate;

	public static final String publishDate_FIELDNAME = "publishDate";
	@FieldDescribe("发布日期")
	@Column( name = ColumnNamePrefix + publishDate_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + publishDate_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date publishDate;

	public static final String documentStatus_FIELDNAME = "documentStatus";
	@FieldDescribe("文档状态：草稿|审核中|已发布|已归档")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + documentStatus_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + documentStatus_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String documentStatus = "草稿";

	public static final String publisher_FIELDNAME = "publisher";
	@FieldDescribe("发布者姓名")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + publisher_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String publisher;

	public static final String permission_FIELDNAME = "permission";
	@FieldDescribe("权限类别：阅读|管理")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + permission_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + permission_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String permission = "所有人";

	public static final String permissionObjectType_FIELDNAME = "permissionObjectType";
	@FieldDescribe("使用者类别：所有人|组织|人员|群组|角色")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + permissionObjectType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + permissionObjectType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String permissionObjectType = "所有人";

	public static final String permissionObjectCode_FIELDNAME = "permissionObjectCode";
	@FieldDescribe("使用者编码：所有人|组织编码|人员UID|群组编码|角色编码")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + permissionObjectCode_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + permissionObjectCode_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String permissionObjectCode = "所有人";

	public static final String permissionObjectName_FIELDNAME = "permissionObjectName";
	@FieldDescribe("使用者名称：所有人|组织名称|人员名称|群组名称|角色名称")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + permissionObjectName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String permissionObjectName = "所有人";

	public static final String updateFlag_FIELDNAME = "updateFlag";
	@FieldDescribe("更新标识")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + updateFlag_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String updateFlag = null;

	public String getAppId() {
		return appId;
	}

	public String getAppName() {
		return appName;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getTitle() {
		return title;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public String getDocumentStatus() {
		return documentStatus;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public void setDocumentStatus(String documentStatus) {
		this.documentStatus = documentStatus;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getDocumentId() {
		return documentId;
	}

	public String getPermissionObjectType() {
		return permissionObjectType;
	}

	public String getPermissionObjectCode() {
		return permissionObjectCode;
	}

	public String getPermissionObjectName() {
		return permissionObjectName;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public void setPermissionObjectType(String permissionObjectType) {
		this.permissionObjectType = permissionObjectType;
	}

	public void setPermissionObjectCode(String permissionObjectCode) {
		this.permissionObjectCode = permissionObjectCode;
	}

	public void setPermissionObjectName(String permissionObjectName) {
		this.permissionObjectName = permissionObjectName;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getUpdateFlag() {
		return updateFlag;
	}

	public void setUpdateFlag(String updateFlag) {
		this.updateFlag = updateFlag;
	}

	public Date getDocCreateDate() {
		return docCreateDate;
	}

	public void setDocCreateDate(Date docCreateDate) {
		this.docCreateDate = docCreateDate;
	}

	public String getCategoryAlias() {
		return categoryAlias;
	}

	public void setCategoryAlias(String categoryAlias) {
		this.categoryAlias = categoryAlias;
	}

}