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
 * 论坛权限信息表
 * 
 * @author LIYI
 */

@Schema(name = "BBSPermissionInfo", description = "论坛权限信息.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.BBSPermissionInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.BBSPermissionInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BBSPermissionInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSPermissionInfo.table;

	public BBSPermissionInfo() {
	}

	public BBSPermissionInfo(String forumId, String forumName, String sectionId, String sectionName,
			String mainSectionId, String mainSectionName, String permissionType, String permissionName,
			String permissionFunction, String permissionCode, String description, Integer orderNumber) {
		super();
		this.forumId = forumId;
		this.forumName = forumName;
		this.sectionId = sectionId;
		this.sectionName = sectionName;
		this.mainSectionId = mainSectionId;
		this.mainSectionName = mainSectionName;
		this.permissionType = permissionType;
		this.permissionName = permissionName;
		this.permissionCode = permissionCode;
		this.description = description;
		this.orderNumber = orderNumber;
		this.permissionFunction = permissionFunction;
	}

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

	public static final String forumId_FIELDNAME = "forumId";
	@FieldDescribe("论坛ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + forumId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + forumId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String forumId = "";

	public static final String forumName_FIELDNAME = "forumName";
	@FieldDescribe("论坛名称")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + forumName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String forumName = "";

	public static final String sectionId_FIELDNAME = "sectionId";
	@FieldDescribe("版块ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + sectionId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sectionId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sectionId = "";

	public static final String sectionName_FIELDNAME = "sectionName";
	@FieldDescribe("版块名称")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + sectionName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sectionName = "";

	public static final String mainSectionId_FIELDNAME = "mainSectionId";
	@FieldDescribe("主版块ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + mainSectionId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + mainSectionId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String mainSectionId = "";

	public static final String mainSectionName_FIELDNAME = "mainSectionName";
	@FieldDescribe("主版块名称")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + mainSectionName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String mainSectionName = "";

	public static final String permissionType_FIELDNAME = "permissionType";
	@FieldDescribe("权限类别：论坛权限|版块权限|其他权限")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + permissionType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + permissionType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String permissionType = "其他权限";

	public static final String permissionFunction_FIELDNAME = "permissionFunction";
	@FieldDescribe("权限功能：属于权限代码的一部分，描述权限的主要功能")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + permissionFunction_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + permissionFunction_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String permissionFunction = "其他权限";

	public static final String permissionName_FIELDNAME = "permissionName";
	@FieldDescribe("权限名称")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + permissionName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + permissionName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String permissionName = "";

	public static final String permissionCode_FIELDNAME = "permissionCode";
	@FieldDescribe("权限代码")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + permissionCode_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + permissionCode_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String permissionCode = "";

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("权限说明")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String description = "";

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("排序号")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer orderNumber = 1;

	public String getForumId() {
		return forumId;
	}

	public void setForumId(String forumId) {
		this.forumId = forumId;
	}

	public String getForumName() {
		return forumName;
	}

	public void setForumName(String forumName) {
		this.forumName = forumName;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getMainSectionId() {
		return mainSectionId;
	}

	public void setMainSectionId(String mainSectionId) {
		this.mainSectionId = mainSectionId;
	}

	public String getMainSectionName() {
		return mainSectionName;
	}

	public void setMainSectionName(String mainSectionName) {
		this.mainSectionName = mainSectionName;
	}

	public String getPermissionType() {
		return permissionType;
	}

	public void setPermissionType(String permissionType) {
		this.permissionType = permissionType;
	}

	public String getPermissionName() {
		return permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

	public String getPermissionCode() {
		return permissionCode;
	}

	public void setPermissionCode(String permissionCode) {
		this.permissionCode = permissionCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getPermissionFunction() {
		return permissionFunction;
	}

	public void setPermissionFunction(String permissionFunction) {
		this.permissionFunction = permissionFunction;
	}

}