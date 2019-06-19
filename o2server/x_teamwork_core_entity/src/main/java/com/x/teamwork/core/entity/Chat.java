package com.x.teamwork.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Chat.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Chat.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Chat extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;

	private static final String TABLE = PersistenceProperties.Chat.table;

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
	public static final String projectId_FIELDNAME = "projectId";
	@FieldDescribe("所属项目ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + projectId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + projectId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String projectId = null;

	public static final String projectTitle_FIELDNAME = "projectTitle";
	@FieldDescribe("项目标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + projectTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String projectTitle = null;

	public static final String taskId_FIELDNAME = "taskId";
	@FieldDescribe("所属工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + taskId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + taskId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String taskId = null;

	public static final String taskTitle_FIELDNAME = "taskTitle";
	@FieldDescribe("工作标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + taskTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String taskTitle = null;

	public static final String sender_FIELDNAME = "sender";
	@FieldDescribe("发送者")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + sender_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + sender_FIELDNAME)
	private String sender = null;

	public static final String target_FIELDNAME = "target";
	@FieldDescribe("目标者")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + target_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + target_FIELDNAME)
	private String target = null;

	public static final String content_FIELDNAME = "content";
	@FieldDescribe("交流内容，小于70个汉字" )
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + content_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String content = null;
	
	public static final String description_FIELDNAME = "description";
	@FieldDescribe("备注说明")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description = null;
	
	public static final String isLob_FIELDNAME = "isLob";
	@FieldDescribe("是否是LOB数据")
	@Column( name = ColumnNamePrefix + isLob_FIELDNAME)
	private Boolean isLob = false;
	
	public static final String isFile_FIELDNAME = "isFile";
	@FieldDescribe("是否是发送的文件")
	@Column( name = ColumnNamePrefix + isFile_FIELDNAME)
	private Boolean isFile = false;
	
	public static final String fileId_FIELDNAME = "fileId";
	@FieldDescribe("发送的文件ID，存储到file应用的文件ID")
	@Column( name = ColumnNamePrefix + fileId_FIELDNAME)
	private Boolean fileId = false;
	
	public static final String deleted_FIELDNAME = "deleted";
	@FieldDescribe("是否已经删除")
	@Column( name = ColumnNamePrefix + deleted_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + deleted_FIELDNAME )
	private Boolean deleted = false;
	
	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getProjectTitle() {
		return projectTitle;
	}

	public void setProjectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskTitle() {
		return taskTitle;
	}

	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsLob() {
		return isLob;
	}

	public void setIsLob(Boolean isLob) {
		this.isLob = isLob;
	}

	public Boolean getIsFile() {
		return isFile;
	}

	public void setIsFile(Boolean isFile) {
		this.isFile = isFile;
	}

	public Boolean getFileId() {
		return fileId;
	}

	public void setFileId(Boolean fileId) {
		this.fileId = fileId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}	
}