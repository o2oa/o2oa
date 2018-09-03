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

	@FieldDescribe("文档ID")
	@Column(name = "xdocumentId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String documentId;

	@FieldDescribe("文档标题")
	@Column(name = "xtitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String title = "无标题";

	@FieldDescribe("应用ID")
	@Column(name = "xappId", length = JpaObject.length_id)
	@Index(name = TABLE + "_appId")
	@CheckPersist(allowEmpty = true)
	private String appId;

	@FieldDescribe("应用名称")
	@Column(name = "xappName", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String appName;

	@FieldDescribe("分类ID")
	@Column(name = "xcategoryId", length = JpaObject.length_id)
	@Index(name = TABLE + "_categoryId")
	@CheckPersist(allowEmpty = true)
	private String categoryId;

	@FieldDescribe("分类名称")
	@Column(name = "xcategoryName", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String categoryName;

	@FieldDescribe("分类别名")
	@Column(name = "xcategoryAlias", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String categoryAlias;

	@FieldDescribe("创建日期")
	@Column(name = "xdocCreateDate")
	@Index(name = TABLE + "_docCreateDate")
	@CheckPersist(allowEmpty = true)
	private Date docCreateDate;

	@FieldDescribe("发布日期")
	@Column(name = "xpublishDate")
	@Index(name = TABLE + "_publishDate")
	@CheckPersist(allowEmpty = true)
	private Date publishDate;

	@FieldDescribe("文档状态：草稿|审核中|已发布|已归档")
	@Column(name = "xdocumentStatus", length = JpaObject.length_16B)
	@Index(name = TABLE + "_documentStatus")
	@CheckPersist(allowEmpty = true)
	private String documentStatus = "草稿";

	@FieldDescribe("发布者姓名")
	@Column(name = "xpublisher", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String publisher;

	@FieldDescribe("权限类别：阅读|管理")
	@Column(name = "xpermission", length = JpaObject.length_16B)
	@Index(name = TABLE + "_permission")
	@CheckPersist(allowEmpty = true)
	private String permission = "所有人";

	@FieldDescribe("使用者类别：所有人|组织|人员|群组|角色")
	@Column(name = "xpermissionObjectType", length = JpaObject.length_16B)
	@Index(name = TABLE + "_permissionObjectType")
	@CheckPersist(allowEmpty = true)
	private String permissionObjectType = "所有人";

	@FieldDescribe("使用者编码：所有人|组织编码|人员UID|群组编码|角色编码")
	@Column(name = "xpermissionObjectCode", length = JpaObject.length_255B)
	@Index(name = TABLE + "_permissionObjectCode")
	@CheckPersist(allowEmpty = true)
	private String permissionObjectCode = "所有人";

	@FieldDescribe("使用者名称：所有人|组织名称|人员名称|群组名称|角色名称")
	@Column(name = "xpermissionObjectName", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String permissionObjectName = "所有人";

	@FieldDescribe("更新标识")
	@Column(name = "xupdateFlag", length = JpaObject.length_32B)
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