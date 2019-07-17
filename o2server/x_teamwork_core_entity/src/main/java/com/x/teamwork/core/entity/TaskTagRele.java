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

/**
 * 工作任务与任务标签关联表
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.TaskTagRele.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.TaskTagRele.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TaskTagRele extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.TaskTagRele.table;

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
	public static final String project_FIELDNAME = "project";
	@FieldDescribe("所属项目ID.")
	@Column(length = length_id, name = ColumnNamePrefix + project_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + project_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String project;
	
	public static final String tagId_FIELDNAME = "tagId";
	@FieldDescribe("标签ID")
	@Column( length = JpaObject.length_id, name = ColumnNamePrefix + tagId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + tagId_FIELDNAME)
	@CheckPersist( allowEmpty = false)
	private String tagId;
	
	public static final String taskId_FIELDNAME = "taskId";
	@FieldDescribe("工作任务ID")
	@Column( length = JpaObject.length_id, name = ColumnNamePrefix + taskId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + taskId_FIELDNAME)
	@CheckPersist( allowEmpty = false)
	private String taskId;
	
	public static final String owner_FIELDNAME = "owner";
	@FieldDescribe("创建者。")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + owner_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + owner_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String owner;
	
	public static final String order_FIELDNAME = "order";
	@FieldDescribe("排序号")
	@Column( name = ColumnNamePrefix + order_FIELDNAME )
	private Integer order;

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}	
}