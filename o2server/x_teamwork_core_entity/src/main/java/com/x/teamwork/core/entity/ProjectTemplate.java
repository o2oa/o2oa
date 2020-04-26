package com.x.teamwork.core.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.OrderColumn;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 项目模板信息
 * 
 * @author O2LJ
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.ProjectTemplate.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.ProjectTemplate.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ProjectTemplate extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.ProjectTemplate.table;

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
	public static final String title_FIELDNAME = "title";
	@FieldDescribe("模板名称")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + title_FIELDNAME)
	private String title;
	
	public static final String order_FIELDNAME = "order";
	@FieldDescribe("排序号")
	@Column( name = ColumnNamePrefix + order_FIELDNAME )
	private Integer order;
	
	public static final String type_FIELDNAME = "type";
	@FieldDescribe("模板类型")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + type_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + type_FIELDNAME)
	@CheckPersist(allowEmpty = true )
	private String type;
	
	public static final String icon_FIELDNAME = "icon";
	@FieldDescribe("图标文件ID")
	@Column( length = JpaObject.length_id, name = ColumnNamePrefix + icon_FIELDNAME)
	@CheckPersist(allowEmpty = true )
	private String icon = null;
	
	public static final String description_FIELDNAME = "description";
	@FieldDescribe("模板类型")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + description_FIELDNAME)
	@CheckPersist(allowEmpty = true )
	private String description;
	
	public static final String owner_FIELDNAME = "owner";
	@FieldDescribe("创建者")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + owner_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + owner_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String owner;
	
	public static final String taskList_FIELDNAME = "taskList";
	@FieldDescribe("默认永道")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + taskList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + taskList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_255B, name = ColumnNamePrefix + taskList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + taskList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> taskList;
	
	public static final String deleted_FIELDNAME = "deleted";
	@FieldDescribe("是否已经删除")
	@Column( name = ColumnNamePrefix + deleted_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + deleted_FIELDNAME )
	private Boolean deleted = false;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public List<String> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<String> taskList) {
		this.taskList = taskList;
	}
	
	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	
}