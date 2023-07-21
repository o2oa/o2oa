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
 * 共享文件记录信息表
 * 
 * @author O2LEE
 */
@Schema(name = "MindShareRecord", description = "脑图共享文件记录信息.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.MindShareRecord.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.MindShareRecord.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class MindShareRecord extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.MindShareRecord.table;

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
	public static final String fileId_FIELDNAME = "fileId";
	@FieldDescribe("文件ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + fileId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + fileId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String fileId = "";

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("文件名称")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String name = "";

	public static final String fileType_FIELDNAME = "fileType";
	@FieldDescribe("文件类型：MIND | FOLDER")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + fileType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + fileType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String fileType = "MIND";

	public static final String shareType_FIELDNAME = "shareType";
	@FieldDescribe("共享类型：分享 | 协作")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + shareType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + shareType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String shareType = "分享";

	public static final String source_FIELDNAME = "source";
	@FieldDescribe("发送者")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + source_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + source_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String source = "";

	public static final String target_FIELDNAME = "target";
	@FieldDescribe("目标用户")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + target_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + target_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String target = "";

	public static final String targetType_FIELDNAME = "targetType";
	@FieldDescribe("目标类型：PERSON | UNIT | GROUP | ROLE")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + targetType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + targetType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String targetType = "PERSON";

	public static final String fileStatus_FIELDNAME = "fileStatus";
	@FieldDescribe("文件类型：正常 | 已删除")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + fileStatus_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + fileStatus_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String fileStatus = "正常";

	public String getFileId() {
		return fileId;
	}

	public String getName() {
		return name;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getFileStatus() {
		return fileStatus;
	}

	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}