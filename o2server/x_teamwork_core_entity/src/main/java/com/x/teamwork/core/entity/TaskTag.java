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
 * 工作任务标签信息
 * 
 * @author O2LEE
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.TaskTag.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.TaskTag.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TaskTag extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.TaskTag.table;

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
	@CheckPersist(allowEmpty = true)
	private String project;
	
	public static final String tag_FIELDNAME = "tag";
	@FieldDescribe("工作任务标签")
	@Column( length = JpaObject.length_64B, name = ColumnNamePrefix + tag_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + tag_FIELDNAME)
	@CheckPersist( allowEmpty = false )
	private String tag;
	
	public static final String color_FIELDNAME = "tagColor";
	@FieldDescribe("标签颜色")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + color_FIELDNAME)
	@CheckPersist( allowEmpty = false )
	private String tagColor;
	
	public static final String owner_FIELDNAME = "owner";
	@FieldDescribe("创建者")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + owner_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + owner_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String owner;

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getTagColor() {
		return tagColor;
	}

	public void setTagColor(String tagColor) {
		this.tagColor = tagColor;
	}

	
}