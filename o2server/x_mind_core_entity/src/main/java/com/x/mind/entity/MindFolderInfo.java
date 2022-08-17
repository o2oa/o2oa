package com.x.mind.entity;

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
 * 脑图文件夹（分类）信息表
 * 
 * @author O2LEE
 */
@Schema(name = "MindFolderInfo", description = "脑图文件夹.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.MindFolderInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.MindFolderInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class MindFolderInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.MindFolderInfo.table;

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
	// public static String[] FLA GS = new String[] { "id" };

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("目录名称")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String name = "";

	public static final String parentId_FIELDNAME = "parentId";
	@FieldDescribe("上级目录Id，默认为‘0’ ")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + parentId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + parentId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String parentId = "";

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("编号，默认为1")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer orderNumber = 0;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("备注信息")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description = "";

	public static final String creator_FIELDNAME = "creator";
	@FieldDescribe("自动生成，创建者")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + creator_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creator_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creator = "";

	public static final String creatorUnit_FIELDNAME = "creatorUnit";
	@FieldDescribe("自动生成，创建者所属组织")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + creatorUnit_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creatorUnit = "";

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getCreator() {
		return creator;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getCreatorUnit() {
		return creatorUnit;
	}

	public void setCreatorUnit(String creatorUnit) {
		this.creatorUnit = creatorUnit;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
}