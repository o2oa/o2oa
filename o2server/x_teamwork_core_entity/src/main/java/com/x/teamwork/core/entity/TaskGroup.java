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
 * 工作任务分组信息
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.TaskGroup.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.TaskGroup.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TaskGroup extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.TaskGroup.table;

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
	
	public static final String name_FIELDNAME = "name";
	@FieldDescribe("工作任务分组名称")
	@Column( length = JpaObject.length_96B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String name;
	
	public static final String order_FIELDNAME = "order";
	@FieldDescribe("排序号")
	@Column( name = ColumnNamePrefix + order_FIELDNAME )
	private Integer order = 0;
	
	public static final String taskTotal_FIELDNAME = "taskTotal";
	@FieldDescribe("任务总数")
	@Column( name = ColumnNamePrefix + taskTotal_FIELDNAME )
	private Integer taskTotal = 0;
	
	public static final String completedTotal_FIELDNAME = "completedTotal";
	@FieldDescribe("已完成数量")
	@Column( name = ColumnNamePrefix + completedTotal_FIELDNAME )
	private Integer completedTotal = 0;
	
	public static final String overtimeTotal_FIELDNAME = "overtimeTotal";
	@FieldDescribe("超时数量")
	@Column( name = ColumnNamePrefix + overtimeTotal_FIELDNAME )
	private Integer overtimeTotal = 0;
	
	public static final String memo_FIELDNAME = "memo";
	@FieldDescribe("分组描述")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + memo_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + memo_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String memo;	
	
	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("创建者，可能为System，如果由系统创建。")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + creatorPerson_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + creatorPerson_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;
	
	public static final String owner_FIELDNAME = "owner";
	@FieldDescribe("拥有者")
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Integer getTaskTotal() {
		return taskTotal;
	}

	public void setTaskTotal(Integer taskTotal) {
		this.taskTotal = taskTotal;
	}

	public Integer getCompletedTotal() {
		return completedTotal;
	}

	public void setCompletedTotal( Integer completedTotal ) {
		this.completedTotal = completedTotal;
	}

	public Integer getOvertimeTotal() {
		return overtimeTotal;
	}

	public void setOvertimeTotal( Integer overtimeTotal ) {
		this.overtimeTotal = overtimeTotal;
	}
	
	public void addTaskTotal( int count ) {
		if( this.taskTotal == null ) {
			this.taskTotal = 0;
		}
		this.taskTotal += count;
	}
	
	public void subTaskTotal( int count ) {
		if( this.taskTotal == null ) {
			this.taskTotal = 0;
		}
		this.taskTotal -= count;
		if( this.taskTotal < 0 ) {
			this.taskTotal = 0;
		}
	}
	
	public void addCompletedTotal( int count ) {
		if( this.completedTotal == null ) {
			this.completedTotal = 0;
		}
		this.completedTotal += count;
	}
	
	public void subCompletedTotal( int count ) {
		if( this.completedTotal == null ) {
			this.completedTotal = 0;
		}
		this.completedTotal -= count;
		if( this.completedTotal < 0 ) {
			this.completedTotal = 0;
		}
	}
	
	public void addOvertimeTotal( int count ) {
		if( this.overtimeTotal == null ) {
			this.overtimeTotal = 0;
		}
		this.overtimeTotal += count;
	}
	
	public void subOvertimeTotal( int count ) {
		if( this.overtimeTotal == null ) {
			this.overtimeTotal = 0;
		}
		this.overtimeTotal -= count;
		if( this.overtimeTotal < 0 ) {
			this.overtimeTotal = 0;
		}
	}
}