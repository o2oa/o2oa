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

/**
 * 内容管理应用目录分类管理员配置表 (取消数据结构，后续删除)
 * 
 * @author 李义
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.AppCategoryAdmin.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.AppCategoryAdmin.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AppCategoryAdmin extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.AppCategoryAdmin.table;

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
	public static final String objectType_FIELDNAME = "objectType";
	@FieldDescribe("对象类别：应用|分类")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + objectType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + objectType_FIELDNAME)
	private String objectType;

	public static final String objectId_FIELDNAME = "objectId";
	@FieldDescribe("对象ID")
	@Column( length = JpaObject.length_id, name = ColumnNamePrefix + objectId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + objectId_FIELDNAME)
	private String objectId;

	public static final String objectName_FIELDNAME = "objectName";
	@FieldDescribe("对象名称")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + objectName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String objectName;

	public static final String adminUid_FIELDNAME = "adminUid";
	@FieldDescribe("管理员UID")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + adminUid_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + adminUid_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String adminUid;

	public static final String adminName_FIELDNAME = "adminName";
	@FieldDescribe("管理员姓名")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + adminName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String adminName;

	public static final String adminLevel_FIELDNAME = "adminLevel";
	@FieldDescribe("管理员级别: ADMIN | DOCUMENTADMIN | PUBLISHER")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + adminLevel_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String adminLevel;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("描述")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String creatorUid_FIELDNAME = "creatorUid";
	@FieldDescribe("创建者UID")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + creatorUid_FIELDNAME)
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
	 * 获取操作者帐号
	 * 
	 * @return
	 */
	public String getAdminUid() {
		return adminUid;
	}

	/**
	 * 设置操作者帐号
	 * 
	 * @param operatorUid
	 */
	public void setAdminUid(String adminUid) {
		this.adminUid = adminUid;
	}

	/**
	 * 获取操作者姓名
	 * 
	 * @return
	 */
	public String getAdminName() {
		return adminName;
	}

	/**
	 * 设置操作者姓名
	 * 
	 * @param operatorName
	 */
	public void setAdminName(String adminName) {
		this.adminName = adminName;
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
	 * 获取用户的管理级别：管理员级别: ADMIN | DOCUMENTADMIN | PUBLISHER
	 * 
	 * @return
	 */
	public String getAdminLevel() {
		return adminLevel;
	}

	/**
	 * 设置用户的管理级别：管理员级别: ADMIN | DOCUMENTADMIN | PUBLISHER
	 * 
	 * @param adminLevel
	 */
	public void setAdminLevel(String adminLevel) {
		this.adminLevel = adminLevel;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

}