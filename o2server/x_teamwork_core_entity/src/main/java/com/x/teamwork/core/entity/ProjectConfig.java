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
import com.x.base.core.project.tools.DateTools;

/**
 * 优先级信息
 * 
 * @author O2LJ
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.ProjectConfig.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.ProjectConfig.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ProjectConfig extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.ProjectConfig.table;

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
		/** 生成默认排序号 */
		if (null == this.order) {
			this.order = DateTools.timeOrderNumber();
		}
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
	public static final String project_FIELDNAME = "project";
	@FieldDescribe("项目信息ID.")
	@Column(length = length_id, name = ColumnNamePrefix + project_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + project_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String project;
	
	public static final String taskCreate_FIELDNAME = "taskCreate";
	@FieldDescribe("新建任务")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + taskCreate_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + taskCreate_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private Boolean taskCreate;
	
	public static final String taskCopy_FIELDNAME = "taskCopy";
	@FieldDescribe("复制任务")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + taskCopy_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + taskCopy_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private Boolean taskCopy;
	
	public static final String taskRemove_FIELDNAME = "taskRemove";
	@FieldDescribe("删除任务")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + taskRemove_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + taskRemove_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private Boolean taskRemove;
	
	public static final String laneCreate_FIELDNAME = "laneCreate";
	@FieldDescribe("新建泳道")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + laneCreate_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + laneCreate_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private Boolean laneCreate;
	
	public static final String laneEdit_FIELDNAME = "laneEdit";
	@FieldDescribe("编辑泳道")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + laneEdit_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + laneEdit_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private Boolean laneEdit;
	
	public static final String laneRemove_FIELDNAME = "laneRemove";
	@FieldDescribe("删除泳道")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + laneRemove_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + laneRemove_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private Boolean laneRemove;
	
	public static final String attachmentUpload_FIELDNAME = "attachmentUpload";
	@FieldDescribe("上传附件")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + attachmentUpload_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + attachmentUpload_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private Boolean attachmentUpload;
	
	public static final String comment_FIELDNAME = "comment";
	@FieldDescribe("允许评论")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + comment_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + comment_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private Boolean comment;
	
	public static final String order_FIELDNAME = "order";
	@FieldDescribe("排序号")
	@Column( name = ColumnNamePrefix + order_FIELDNAME )
	private Integer order;
	
	public static final String owner_FIELDNAME = "owner";
	@FieldDescribe("创建者")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + owner_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + owner_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String owner;
	
	public static final String description_FIELDNAME = "description";
	@FieldDescribe("描述.")
	@Column(length = length_id, name = ColumnNamePrefix + description_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}
	
	public Boolean getTaskCreate() {
		return taskCreate;
	}

	public void setTaskCreate(Boolean taskCreate) {
		this.taskCreate = taskCreate;
	}
	
	public Boolean getTaskCopy() {
		return taskCopy;
	}

	public void setTaskCopy(Boolean taskCopy) {
		this.taskCopy = taskCopy;
	}
	
	public Boolean getTaskRemove() {
		return taskRemove;
	}

	public void setTaskRemove(Boolean taskRemove) {
		this.taskRemove = taskRemove;
	}
	
	public Boolean getLaneCreate() {
		return laneCreate;
	}

	public void setLaneCreate(Boolean laneCreate) {
		this.laneCreate = laneCreate;
	}
	
	public Boolean getLaneEdit() {
		return laneEdit;
	}

	public void setLaneEdit(Boolean laneEdit) {
		this.laneEdit = laneEdit;
	}
	
	public Boolean getLaneRemove() {
		return laneRemove;
	}

	public void setLaneRemove(Boolean laneRemove) {
		this.laneRemove = laneRemove;
	}

	public Boolean getAttachmentUpload() {
		return attachmentUpload;
	}

	public void setAttachmentUpload(Boolean attachmentUpload) {
		this.attachmentUpload = attachmentUpload;
	}
	
	public Boolean getComment() {
		return comment;
	}

	public void setComment(Boolean comment) {
		this.comment = comment;
	}
	
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
}