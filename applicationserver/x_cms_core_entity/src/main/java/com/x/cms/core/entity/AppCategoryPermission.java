package com.x.cms.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 内容管理应用目录分类可见使用范围配置表 (取消数据结构，后续删除)
 * 
 * @author 李义
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.AppCategoryPermission.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.AppCategoryPermission.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AppCategoryPermission extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.AppCategoryPermission.table;

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
	@FieldDescribe("对象类别：应用|分类")
	@Column(name = "xobjectType", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String objectType;

	@FieldDescribe("权限类别：VIEW|PUBLISH")
	@Column(name = "xpermission", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String permission = "VIEW";

	@FieldDescribe("所属栏目ID")
	@Column(name = "xappId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String appId;

	@FieldDescribe("对象ID")
	@Column(name = "xobjectId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String objectId;

	@FieldDescribe("对象名称")
	@Column(name = "xobjectName", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String objectName;

	@FieldDescribe("使用者类别：组织|人员|群组|角色")
	@Column(name = "xusedObjectType", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String usedObjectType;

	@FieldDescribe("使用者编码：顶层组织OU、组织OU或者人员UID")
	@Column(name = "xusedObjectCode", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String usedObjectCode;

	@FieldDescribe("使用者名称：组织名称|人员姓名")
	@Column(name = "xusedObjectName", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String usedObjectName;

	@FieldDescribe("描述")
	@Column(name = "xdescription", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String description;

	@FieldDescribe("创建者UID")
	@Column(name = "xcreatorUid", length = JpaObject.length_64B)
	@CheckPersist(allowEmpty = true)
	private String creatorUid;

	/**
	 * 获取操作对象类别
	 * 
	 * @return
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * 设置操作对象类别
	 * 
	 * @param objectType
	 */
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	/**
	 * 获取操作对象ID
	 * 
	 * @return
	 */
	public String getObjectId() {
		return objectId;
	}

	/**
	 * 设置操作对象ID
	 * 
	 * @param objectId
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	/**
	 * 获取文字描述
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置文字描述
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取配置创建者帐号
	 * 
	 * @return
	 */
	public String getCreatorUid() {
		return creatorUid;
	}

	/**
	 * 设置配置创建者帐号
	 * 
	 * @param creatorUid
	 */
	public void setCreatorUid(String creatorUid) {
		this.creatorUid = creatorUid;
	}

	/**
	 * 获取使用者类别（组织（ORGAN）|人员（USER）
	 * 
	 * @return
	 */
	public String getUsedObjectType() {
		return usedObjectType;
	}

	/**
	 * 设置使用者类别（组织（ORGAN）|人员（USER）
	 * 
	 * @param usedObjectType
	 */
	public void setUsedObjectType(String usedObjectType) {
		this.usedObjectType = usedObjectType;
	}

	/**
	 * 获取使用者编码（组织OU|人员UID）
	 * 
	 * @return
	 */
	public String getUsedObjectCode() {
		return usedObjectCode;
	}

	/**
	 * 设置使用者编码（组织OU|人员UID）
	 * 
	 * @param usedObjectCode
	 */
	public void setUsedObjectCode(String usedObjectCode) {
		this.usedObjectCode = usedObjectCode;
	}

	/**
	 * 获取使用者名称
	 * 
	 * @return
	 */
	public String getUsedObjectName() {
		return usedObjectName;
	}

	/**
	 * 设置使用者名称
	 * 
	 * @param usedObjectName
	 */
	public void setUsedObjectName(String usedObjectName) {
		this.usedObjectName = usedObjectName;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

}