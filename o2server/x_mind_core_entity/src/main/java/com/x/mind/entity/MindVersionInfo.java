package com.x.mind.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 脑图版本信息表
 * 
 * @author O2LEE
 */
@Schema(name = "MindVersionInfo", description = "脑图版本信息.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.MindVersionInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.MindVersionInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class MindVersionInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.MindVersionInfo.table;

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
		if (null == this.getCreateTime()) {
			this.setCreateTime(new Date());
		}
		this.setSequence(StringUtils.join(DateTools.compact(this.getCreateTime()), this.getId()));
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
	public static final String mindId_FIELDNAME = "mindId";
	@FieldDescribe("脑图文件ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + mindId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + mindId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String mindId = "";

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("脑图名称")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String name = "";

	public static final String folderId_FIELDNAME = "folderId";
	@FieldDescribe("所属目录")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + folderId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String folderId = "";

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("备注信息")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description = "";

	public static final String creator_FIELDNAME = "creator";
	@FieldDescribe("自动生成，创建者")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + creator_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creator = "";

	public static final String creatorUnit_FIELDNAME = "creatorUnit";
	@FieldDescribe("自动生成，创建者所属组织")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + creatorUnit_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creatorUnit = "";

	public static final String fileVersion_FIELDNAME = "fileVersion";
	@FieldDescribe("自动生成，版本号")
	@Column(name = ColumnNamePrefix + fileVersion_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer fileVersion = 1;

	public static final String shared_FIELDNAME = "shared";
	@FieldDescribe("自动生成，是否已经分享")
	@Column(name = "xshared")
	private Boolean shared = false;

	public String getMindId() {
		return mindId;
	}

	public String getName() {
		return name;
	}

	public String getFolderId() {
		return folderId;
	}

	public String getDescription() {
		return description;
	}

	public String getCreator() {
		return creator;
	}

	public String getCreatorUnit() {
		return creatorUnit;
	}

	public Integer getFileVersion() {
		return fileVersion;
	}

	public Boolean getShared() {
		return shared;
	}

	public void setMindId(String mindId) {
		this.mindId = mindId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public void setCreatorUnit(String creatorUnit) {
		this.creatorUnit = creatorUnit;
	}

	public void setFileVersion(Integer fileVersion) {
		this.fileVersion = fileVersion;
	}

	public void setShared(Boolean shared) {
		this.shared = shared;
	}
}