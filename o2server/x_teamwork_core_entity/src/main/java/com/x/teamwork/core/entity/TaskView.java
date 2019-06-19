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
 * 工作任务组信息
 * 
 * @author O2LEE
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.TodoList.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.TodoList.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TaskView extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.TodoList.table;

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
	@FieldDescribe("任务列表名称")
	@Column( length = JpaObject.length_96B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	private String name;
	
	public static final String workCompleted_FIELDNAME = "workCompleted";
	@FieldDescribe("是否已完成：-1-全部， 1-是，0-否.")
	@Column( name = ColumnNamePrefix + workCompleted_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workCompleted_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer workCompleted = -1;
	
	public static final String workOverTime_FIELDNAME = "workOverTime";
	@FieldDescribe("是否已超时：-1-全部， 1-是，0-否.")
	@Column( name = ColumnNamePrefix + workOverTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workOverTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer workOverTime = -1;
	
	public static final String isExcutor_FIELDNAME = "isExcutor";
	@FieldDescribe("查询负责的项目，如果为false，则为所有参与的项目范围")
	@Column( name = ColumnNamePrefix + isExcutor_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + isExcutor_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean isExcutor = false;
	
	public static final String choosePriority_FIELDNAME = "choosePriority";
	@FieldDescribe("筛选优先级：普通、紧急、特急")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + choosePriority_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + choosePriority_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_32B, name = ColumnNamePrefix + choosePriority_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + choosePriority_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> choosePriority;
	
	public static final String chooseWorkTag_FIELDNAME = "chooseWorkTag";
	@FieldDescribe("筛选标签：自定义标签名")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + chooseWorkTag_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + chooseWorkTag_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_32B, name = ColumnNamePrefix + chooseWorkTag_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + chooseWorkTag_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> chooseWorkTag;
	
	public static final String order_FIELDNAME = "order";
	@FieldDescribe("排序号")
	@Column( name = ColumnNamePrefix + order_FIELDNAME )
	private Integer order;
	
	public static final String memo_FIELDNAME = "memo";
	@FieldDescribe("列表描述")
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

	public List<String> getChoosePriority() {
		return choosePriority;
	}

	public void setChoosePriority(List<String> choosePriority) {
		this.choosePriority = choosePriority;
	}

	public List<String> getChooseWorkTag() {
		return chooseWorkTag;
	}

	public void setChooseWorkTag(List<String> chooseWorkTag) {
		this.chooseWorkTag = chooseWorkTag;
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

	public Integer getWorkCompleted() {
		return workCompleted;
	}

	public void setWorkCompleted(Integer workCompleted) {
		this.workCompleted = workCompleted;
	}

	public Integer getWorkOverTime() {
		return workOverTime;
	}

	public void setWorkOverTime(Integer workOverTime) {
		this.workOverTime = workOverTime;
	}

	public Boolean getIsExcutor() {
		return isExcutor;
	}

	public void setIsExcutor(Boolean isExcutor) {
		this.isExcutor = isExcutor;
	}
}