package com.x.bbs.entity;

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
 * 论坛角色人员信息表
 * 
 * @author LIYI
 */
@Schema(name = "BBSUserRole", description = "论坛角色人员信息.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.BBSUserRole.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.BBSUserRole.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BBSUserRole extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSUserRole.table;

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
	 * =========================================================================
	 * ========= 以上为 JpaObject 默认字段
	 * =========================================================================
	 * =========
	 */

	/*
	 * =========================================================================
	 * ========= 以下为具体不同的业务及数据表字段要求
	 * =========================================================================
	 * =========
	 */
	public static final String forumId_FIELDNAME = "forumId";
	@FieldDescribe("论坛ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + forumId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + forumId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String forumId = "";

	public static final String sectionId_FIELDNAME = "sectionId";
	@FieldDescribe("版块ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + sectionId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sectionId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sectionId = "";

	public static final String mainSectionId_FIELDNAME = "mainSectionId";
	@FieldDescribe("主版块ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + mainSectionId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + mainSectionId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String mainSectionId = "";

	public static final String topUnitName_FIELDNAME = "topUnitName";
	@FieldDescribe("对象所属顶层组织名称")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + topUnitName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + topUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String topUnitName = null;

	public static final String unitName_FIELDNAME = "unitName";
	@FieldDescribe("对象所属组织名称")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + unitName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + unitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String unitName = null;

	public static final String objectName_FIELDNAME = "objectName";
	@FieldDescribe("对象名称：姓名，组织,群组名等等")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + objectName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String objectName = null;

	public static final String objectType_FIELDNAME = "objectType";
	@FieldDescribe("对象类别：人员|组织|群组")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + objectType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + objectType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String objectType = null;

	public static final String uniqueId_FIELDNAME = "uniqueId";
	@FieldDescribe("对象唯一标识")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + uniqueId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + uniqueId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String uniqueId = null;

	public static final String roleId_FIELDNAME = "roleId";
	@FieldDescribe("角色Id")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + roleId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + roleId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String roleId = null;

	public static final String roleCode_FIELDNAME = "roleCode";
	@FieldDescribe("角色代码")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + roleCode_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + roleCode_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String roleCode = null;

	public static final String roleName_FIELDNAME = "roleName";
	@FieldDescribe("角色名称")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + roleName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String roleName = null;

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("排序号")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer orderNumber = 1;

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getTopUnitName() {
		return topUnitName;
	}

	public void setTopUnitName(String topUnitName) {
		this.topUnitName = topUnitName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getForumId() {
		return forumId;
	}

	public String getSectionId() {
		return sectionId;
	}

	public String getMainSectionId() {
		return mainSectionId;
	}

	public void setForumId(String forumId) {
		this.forumId = forumId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public void setMainSectionId(String mainSectionId) {
		this.mainSectionId = mainSectionId;
	}

}