package com.x.teamwork.core.entity;

import java.util.Date;

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

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Review.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Review.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Review extends SliceJpaObject {

	private static final long serialVersionUID = -570048661936488247L;

	private static final String TABLE = PersistenceProperties.Review.table;

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

	/* 以上为 JpaObject 默认字段 */
	public void onPersist() throws Exception {
	}

	public static final String taskId_FIELDNAME = "taskId";
	@FieldDescribe("工作任务ID.")
	@Column(length = length_id, name = ColumnNamePrefix + taskId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + taskId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String taskId;
	
	public static final String taskSequence_FIELDNAME = "taskSequence";
	@FieldDescribe("工作任务序列")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + taskSequence_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle +taskSequence_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String taskSequence;
	
	public static final String permissionObj_FIELDNAME = "permissionObj";
	@FieldDescribe("权限拥有者")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + permissionObj_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + permissionObj_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String permissionObj;
	
	public static final String permissionObjType_FIELDNAME = "permissionObjType";
	@FieldDescribe("权限拥有者类型: PERSON|IDENTITY|UNIT|GROUP|ROLE")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + permissionObjType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String permissionObjType;	
	
	public static final String project_FIELDNAME = "project";
	@FieldDescribe("所属项目ID.")
	@Column(length = length_id, name = ColumnNamePrefix + project_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + project_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String project;
	
	public static final String projectName_FIELDNAME = "projectName";
	@FieldDescribe("所属项目名称.")
	@Column(length = length_id, name = ColumnNamePrefix + projectName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String projectName;
	
	public static final String parent_FIELDNAME = "parent";
	@FieldDescribe("父级工作任务ID.")
	@Column(length = length_id, name = ColumnNamePrefix + parent_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + parent_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String parent;	
	
	public static final String name_FIELDNAME = "name";
	@FieldDescribe("工作任务名称")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private String name;
	
	public static final String startTime_FIELDNAME = "startTime";
	@FieldDescribe("工作开始时间")
	@Column( name = ColumnNamePrefix + startTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + startTime_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private Date startTime;
	
	public static final String endTime_FIELDNAME = "endTime";
	@FieldDescribe("工作开始时间")
	@Column( name = ColumnNamePrefix + endTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + endTime_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private Date endTime;
	
	public static final String tagContent_FIELDNAME = "tagContent";
	@FieldDescribe("展示用，工作任务的标签信息，用#号分隔")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + tagContent_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + tagContent_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private String tagContent = "";
	
	public static final String priority_FIELDNAME = "priority";
	@FieldDescribe("工作等级：普通、紧急、特急")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + priority_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + priority_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private String priority = "普通";
	
	public static final String workStatus_FIELDNAME = "workStatus";
	@FieldDescribe("工作状态：草稿、未开始、执行中、已完成、已挂起、已取消")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + workStatus_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workStatus_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private String workStatus = "草稿";
	
	public static final String completed_FIELDNAME = "completed";
	@FieldDescribe("是否已完成")
	@Column( name = ColumnNamePrefix + completed_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + completed_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private Boolean completed = false;
	
	public static final String claimed_FIELDNAME = "claimed";
	@FieldDescribe("是否已认领")
	@Column( name = ColumnNamePrefix + claimed_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + claimed_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private Boolean claimed = false;
	
	public static final String overtime_FIELDNAME = "overtime";
	@FieldDescribe("是否已超时")
	@Column( name = ColumnNamePrefix + overtime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + overtime_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private Boolean overtime = false;
	
	public static final String deleted_FIELDNAME = "deleted";
	@FieldDescribe("是否已经删除")
	@Column( name = ColumnNamePrefix + deleted_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + deleted_FIELDNAME )
	private Boolean deleted = false;
	
	public static final String remindRelevance_FIELDNAME = "remindRelevance";
	@FieldDescribe("提醒关联任务")
	@Column( name = ColumnNamePrefix + remindRelevance_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + remindRelevance_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private Boolean remindRelevance;
	
	public static final String archive_FIELDNAME = "archive";
	@FieldDescribe("是否已经归档")
	@Column( name = ColumnNamePrefix + archive_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + archive_FIELDNAME )
	private Boolean archive = false;
	
	public static final String progress_FIELDNAME = "progress";
	@FieldDescribe("工作进度：记录4位数，显示的时候除以100")
	@Column( name = ColumnNamePrefix + progress_FIELDNAME)
	private Integer progress = 0;
	
	public static final String order_FIELDNAME = "order";
	@FieldDescribe("排序号")
	@Column( name = ColumnNamePrefix + order_FIELDNAME )
	private Integer order = 0;
	
	public static final String executor_FIELDNAME = "executor";
	@FieldDescribe("执行者和负责人")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + executor_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + executor_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String executor;	
	
	public static final String executorIdentity_FIELDNAME = "executorIdentity";
	@FieldDescribe("执行者|负责人身份")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + executorIdentity_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + executorIdentity_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String executorIdentity;	
	
	public static final String executorUnit_FIELDNAME = "executorUnit";
	@FieldDescribe("执行者|负责人所属组织")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + executorUnit_FIELDNAME )
	@Index( name = TABLE + IndexNameMiddle + executorUnit_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String executorUnit;	
	
	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("创建者，可能为System，如果由系统创建。")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + creatorPerson_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + creatorPerson_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;

	public static final String memoString64_1_FIELDNAME = "memoString64_1";
	@FieldDescribe("备用字符串64属性1.")
	@Column(length = length_64B, name = ColumnNamePrefix + memoString64_1_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoString64_1 = "";
	
	public static final String memoString64_2_FIELDNAME = "memoString64_2";
	@FieldDescribe("备用字符串64属性2.")
	@Column(length = length_64B, name = ColumnNamePrefix + memoString64_2_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoString64_2 = "";
	
	public static final String memoString64_3_FIELDNAME = "memoString64_3";
	@FieldDescribe("备用字符串64属性3.")
	@Column(length = length_255B, name = ColumnNamePrefix + memoString64_3_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoString64_3 = "";
	
	public static final String memoString255_1_FIELDNAME = "memoString255_1";
	@FieldDescribe("备用字符串255属性1.")
	@Column(length = length_255B, name = ColumnNamePrefix + memoString255_1_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoString255_1 = "";
	
	public static final String memoString255_2_FIELDNAME = "memoString255_2";
	@FieldDescribe("备用字符串255属性2.")
	@Column(length = length_255B, name = ColumnNamePrefix + memoString255_2_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoString255_2 = "";
	
	public static final String memoInteger1_FIELDNAME = "memoInteger1";
	@FieldDescribe("备用整型属性1.")
	@Column( name = ColumnNamePrefix + memoInteger1_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer memoInteger1 = 0;
	
	public static final String memoInteger2_FIELDNAME = "memoInteger2";
	@FieldDescribe("备用整型属性2.")
	@Column( name = ColumnNamePrefix + memoInteger2_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer memoInteger2 = 0;
	
	public static final String memoInteger3_FIELDNAME = "memoInteger3";
	@FieldDescribe("备用整型属性3.")
	@Column( name = ColumnNamePrefix + memoInteger3_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer memoInteger3 = 0;
	
	public static final String memoDouble1_FIELDNAME = "memoDouble1";
	@FieldDescribe("备用Double属性1.")
	@Column( name = ColumnNamePrefix + memoDouble1_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double memoDouble1 = 0.0;
	
	public static final String memoDouble2_FIELDNAME = "memoDouble2";
	@FieldDescribe("备用Double属性2.")
	@Column( name = ColumnNamePrefix + memoDouble2_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double memoDouble2 = 0.0;
	
	public Boolean getRemindRelevance() {
		return remindRelevance;
	}

	public void setRemindRelevance(Boolean remindRelevance) {
		this.remindRelevance = remindRelevance;
	}

	public String getTagContent() {
		return tagContent;
	}

	public void setTagContent(String tagContent) {
		this.tagContent = tagContent;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	public Boolean getOvertime() {
		return overtime;
	}

	public void setOvertime(Boolean overtime) {
		this.overtime = overtime;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Integer getProgress() {
		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getExecutor() {
		return executor;
	}

	public void setExecutor(String executor) {
		this.executor = executor;
	}

	public String getExecutorIdentity() {
		return executorIdentity;
	}

	public void setExecutorIdentity(String executorIdentity) {
		this.executorIdentity = executorIdentity;
	}

	public String getExecutorUnit() {
		return executorUnit;
	}

	public void setExecutorUnit(String executorUnit) {
		this.executorUnit = executorUnit;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public String getMemoString64_1() {
		return memoString64_1;
	}

	public void setMemoString64_1(String memoString64_1) {
		this.memoString64_1 = memoString64_1;
	}

	public String getMemoString64_2() {
		return memoString64_2;
	}

	public void setMemoString64_2(String memoString64_2) {
		this.memoString64_2 = memoString64_2;
	}

	public String getMemoString64_3() {
		return memoString64_3;
	}

	public void setMemoString64_3(String memoString64_3) {
		this.memoString64_3 = memoString64_3;
	}

	public String getMemoString255_1() {
		return memoString255_1;
	}

	public void setMemoString255_1(String memoString255_1) {
		this.memoString255_1 = memoString255_1;
	}

	public String getMemoString255_2() {
		return memoString255_2;
	}

	public void setMemoString255_2(String memoString255_2) {
		this.memoString255_2 = memoString255_2;
	}

	public Integer getMemoInteger1() {
		return memoInteger1;
	}

	public void setMemoInteger1(Integer memoInteger1) {
		this.memoInteger1 = memoInteger1;
	}

	public Integer getMemoInteger2() {
		return memoInteger2;
	}

	public void setMemoInteger2(Integer memoInteger2) {
		this.memoInteger2 = memoInteger2;
	}

	public Integer getMemoInteger3() {
		return memoInteger3;
	}

	public void setMemoInteger3(Integer memoInteger3) {
		this.memoInteger3 = memoInteger3;
	}

	public Double getMemoDouble1() {
		return memoDouble1;
	}

	public void setMemoDouble1(Double memoDouble1) {
		this.memoDouble1 = memoDouble1;
	}

	public Double getMemoDouble2() {
		return memoDouble2;
	}

	public void setMemoDouble2(Double memoDouble2) {
		this.memoDouble2 = memoDouble2;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getPermissionObj() {
		return permissionObj;
	}

	public void setPermissionObj(String permissionObj) {
		this.permissionObj = permissionObj;
	}

	public String getPermissionObjType() {
		return permissionObjType;
	}

	public void setPermissionObjType(String permissionObjType) {
		this.permissionObjType = permissionObjType;
	}

	public String getTaskSequence() {
		return taskSequence;
	}

	public void setTaskSequence(String taskSequence) {
		this.taskSequence = taskSequence;
	}

	public Boolean getArchive() {
		return archive;
	}

	public void setArchive(Boolean archive) {
		this.archive = archive;
	}

	public Boolean getClaimed() {
		return claimed;
	}

	public void setClaimed(Boolean claimed) {
		this.claimed = claimed;
	}
	
}