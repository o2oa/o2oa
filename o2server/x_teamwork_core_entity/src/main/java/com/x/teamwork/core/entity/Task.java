package com.x.teamwork.core.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
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
import com.x.base.core.project.tools.ListTools;

/**
 * 工作任务信息
 * 
 * @author O2LEE
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Task.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Task.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Task extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.Task.table;

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
	@FieldDescribe("工作任务名称（40字）")
	@Column( length = JpaObject.length_128B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private String name;
	
	public static final String summay_FIELDNAME = "summay";
	@FieldDescribe("工作任务概括（80字）")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + summay_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + summay_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private String summay;
	
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
	
	public static final String priority_FIELDNAME = "priority";
	@FieldDescribe("工作等级：普通 | 紧急 | 特急")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + priority_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + priority_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private String priority = "普通";
	
	public static final String workStatus_FIELDNAME = "workStatus";
	@FieldDescribe("工作状态：执行中- processing | 已完成- completed | 已归档- archived")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + workStatus_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workStatus_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private String workStatus = "processing";
	
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
	
	public static final String remindRelevance_FIELDNAME = "remindRelevance";
	@FieldDescribe("提醒关联任务")
	@Column( name = ColumnNamePrefix + remindRelevance_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + remindRelevance_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private Boolean remindRelevance;
	
	public static final String deleted_FIELDNAME = "deleted";
	@FieldDescribe("是否已经删除")
	@Column( name = ColumnNamePrefix + deleted_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + deleted_FIELDNAME )
	private Boolean deleted = false;
	
	public static final String archive_FIELDNAME = "archive";
	@FieldDescribe("是否已经归档")
	@Column( name = ColumnNamePrefix + archive_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + archive_FIELDNAME )
	private Boolean archive = false;
	
	public static final String reviewed_FIELDNAME = "reviewed";
	@FieldDescribe("是否检查过review信息")
	@Column( name = ColumnNamePrefix + reviewed_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + reviewed_FIELDNAME )
	private Boolean reviewed = false;
	
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
	
	public static final String participantList_FIELDNAME = "participantList";
	@FieldDescribe("工作任务参与者")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + participantList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + participantList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + participantList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + participantList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> participantList;
	
	public static final String manageablePersonList_FIELDNAME = "manageablePersonList";
	@FieldDescribe("管理者")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + manageablePersonList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + manageablePersonList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + manageablePersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + manageablePersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> manageablePersonList;

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
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

	public Boolean getClaimed() {
		return claimed;
	}

	public void setClaimed(Boolean claimed) {
		this.claimed = claimed;
	}

	public Boolean getOvertime() {
		return overtime;
	}

	public void setOvertime(Boolean overtime) {
		this.overtime = overtime;
	}

	public Boolean getRemindRelevance() {
		return remindRelevance;
	}

	public void setRemindRelevance(Boolean remindRelevance) {
		this.remindRelevance = remindRelevance;
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

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public List<String> getManageablePersonList() {
		return manageablePersonList;
	}

	public void setManageablePersonList(List<String> manageablePersonList) {
		this.manageablePersonList = manageablePersonList;
	}

	public String getSummay() {
		return summay;
	}

	public void setSummay(String summay) {
		this.summay = summay;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public void addManageablePerson( String distinguishedName ) {
		this.manageablePersonList = addStringToList( distinguishedName , this.manageablePersonList );
	}
	public void addManageablePerson( List<String> distinguishedNames ) {
		this.manageablePersonList = addListToList( distinguishedNames , this.manageablePersonList );
	}
	
	public void removeManageablePerson( String distinguishedName ) {
		this.manageablePersonList = removeStringFromList( distinguishedName , this.manageablePersonList );
	}
	
	public void removeManageablePerson( List<String> distinguishedNames ) {
		this.manageablePersonList = removeListFromList( distinguishedNames , this.manageablePersonList );
	}
	
	public void addParticipant( String distinguishedName ) {
		this.participantList = addStringToList( distinguishedName , this.participantList );
	}
	public void removeParticipant( String distinguishedName ) {
		this.participantList = removeStringFromList( distinguishedName , this.participantList );
	}
	public List<String> getParticipantList() {
		return participantList;
	}

	public void setParticipantList(List<String> participantList) {
		this.participantList = participantList;
	}

	private List<String> addStringToList( String source, List<String> targetList ){
		if( targetList == null ) {
			targetList = new ArrayList<>();
		}
		if( StringUtils.isEmpty( source )) {
			return targetList;
		}
		if( !targetList.contains( source )) {
			targetList.add( source );
		}
		return targetList;
	}
	
	private List<String> addListToList( List<String> sourceList, List<String> targetList ){
		if( targetList == null ) {
			targetList = new ArrayList<>();
		}
		if( ListTools.isEmpty( sourceList )) {
			return targetList;
		}
		if( ListTools.isNotEmpty( sourceList )) {
			for( String source : sourceList ) {
				if( !targetList.contains( source )) {
					targetList.add(source);
				}
			}
		}
		return targetList;
	}
	
	private List<String> removeStringFromList( String source, List<String> targetList ){
		if( targetList == null ) {
			targetList = new ArrayList<>();
		}
		if( StringUtils.isEmpty( source )) {
			return targetList;
		}
		if( targetList.contains( source )) {
			targetList.remove( source );
		}
		return targetList;
	}
	
	private List<String> removeListFromList( List<String> sourceList, List<String> targetList ){
		if( targetList == null ) {
			targetList = new ArrayList<>();
		}
		if( ListTools.isEmpty( sourceList )) {
			return targetList;
		}
		List<String> result = new ArrayList<>();
		for( String target : targetList ) {
			if( !sourceList.contains( target )) {
				result.add( target );
			}
		}
		targetList = result;
		return targetList;
	}

	public Boolean getArchive() {
		return archive;
	}

	public void setArchive(Boolean archive) {
		this.archive = archive;
	}

	public Boolean getReviewed() {
		return reviewed;
	}

	public void setReviewed(Boolean reviewed) {
		this.reviewed = reviewed;
	}
}