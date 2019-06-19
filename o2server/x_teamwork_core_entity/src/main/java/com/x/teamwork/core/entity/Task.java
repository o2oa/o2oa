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
	
	public static final String tags_FIELDNAME = "tags";
	@FieldDescribe("工作标签：自定义标签")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + tags_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + tags_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + tags_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + tags_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> tags;
	
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
	
	public static final String participantPersonList_FIELDNAME = "participantPersonList";
	@FieldDescribe("参与人员")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + participantPersonList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + participantPersonList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + participantPersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + participantPersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> participantPersonList;
	
	public static final String participantIdentityList_FIELDNAME = "participantIdentityList";
	@FieldDescribe("参与人员身份")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + participantIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + participantIdentityList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + participantIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + participantIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> participantIdentityList;

	public static final String participantUnitList_FIELDNAME = "participantUnitList";
	@FieldDescribe("参与组织")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + participantUnitList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + participantUnitList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + participantUnitList_FIELDNAME )
	@ElementIndex(name = TABLE + IndexNameMiddle + participantUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> participantUnitList;
	
	public static final String participantGroupList_FIELDNAME = "participantGroupList";
	@FieldDescribe("参与群组")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + participantGroupList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + participantGroupList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + participantGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + participantGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> participantGroupList;
	
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

	public static final String memoString64_1_FIELDNAME = "memoString64_1";
	@FieldDescribe("备用字符串64属性1.")
	@Column(length = length_64B, name = ColumnNamePrefix + memoString64_1_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoString64_1;
	
	public static final String memoString64_2_FIELDNAME = "memoString64_2";
	@FieldDescribe("备用字符串64属性2.")
	@Column(length = length_64B, name = ColumnNamePrefix + memoString64_2_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoString64_2;
	
	public static final String memoString64_3_FIELDNAME = "memoString64_3";
	@FieldDescribe("备用字符串64属性3.")
	@Column(length = length_255B, name = ColumnNamePrefix + memoString64_3_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoString64_3;
	
	public static final String memoString255_1_FIELDNAME = "memoString255_1";
	@FieldDescribe("备用字符串255属性1.")
	@Column(length = length_255B, name = ColumnNamePrefix + memoString255_1_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoString255_1;
	
	public static final String memoString255_2_FIELDNAME = "memoString255_2";
	@FieldDescribe("备用字符串255属性2.")
	@Column(length = length_255B, name = ColumnNamePrefix + memoString255_2_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoString255_2;
	
	public static final String memoInteger1_FIELDNAME = "memoInteger1";
	@FieldDescribe("备用整型属性1.")
	@Column( name = ColumnNamePrefix + memoInteger1_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoInteger1;
	
	public static final String memoInteger2_FIELDNAME = "memoInteger2";
	@FieldDescribe("备用整型属性2.")
	@Column( name = ColumnNamePrefix + memoInteger2_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoInteger2;
	
	public static final String memoInteger3_FIELDNAME = "memoInteger3";
	@FieldDescribe("备用整型属性3.")
	@Column( name = ColumnNamePrefix + memoInteger3_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoInteger3;
	
	public static final String memoDouble1_FIELDNAME = "memoDouble1";
	@FieldDescribe("备用Double属性1.")
	@Column( name = ColumnNamePrefix + memoDouble1_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoDouble1;
	
	public static final String memoDouble2_FIELDNAME = "memoDouble2";
	@FieldDescribe("备用Double属性2.")
	@Column( name = ColumnNamePrefix + memoDouble2_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoDouble2;
	
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

	public List<String> getParticipantPersonList() {
		return participantPersonList;
	}

	public void setParticipantPersonList(List<String> participantPersonList) {
		this.participantPersonList = participantPersonList;
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

	public List<String> getParticipantIdentityList() {
		return participantIdentityList;
	}

	public void setParticipantIdentityList(List<String> participantIdentityList) {
		this.participantIdentityList = participantIdentityList;
	}

	public List<String> getParticipantUnitList() {
		return participantUnitList;
	}

	public void setParticipantUnitList(List<String> participantUnitList) {
		this.participantUnitList = participantUnitList;
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

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
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

	public List<String> getParticipantGroupList() {
		return participantGroupList;
	}

	public void setParticipantGroupList(List<String> participantGroupList) {
		this.participantGroupList = participantGroupList;
	}

	public String getMemoInteger1() {
		return memoInteger1;
	}

	public void setMemoInteger1(String memoInteger1) {
		this.memoInteger1 = memoInteger1;
	}

	public String getMemoInteger2() {
		return memoInteger2;
	}

	public void setMemoInteger2(String memoInteger2) {
		this.memoInteger2 = memoInteger2;
	}

	public String getMemoInteger3() {
		return memoInteger3;
	}

	public void setMemoInteger3(String memoInteger3) {
		this.memoInteger3 = memoInteger3;
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

	public String getMemoDouble1() {
		return memoDouble1;
	}

	public void setMemoDouble1(String memoDouble1) {
		this.memoDouble1 = memoDouble1;
	}

	public String getMemoDouble2() {
		return memoDouble2;
	}

	public void setMemoDouble2(String memoDouble2) {
		this.memoDouble2 = memoDouble2;
	}	

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void addTag( String tag ) {
		this.tags = addStringToList( tag , this.tags );
	}
	public void addTags( List<String> tags ) {
		this.tags = addListToList( tags , this.tags );
	}
	
	public void removeTaskTag( String tag ) {
		this.tags = removeStringFromList( tag , this.tags );
	}
	
	public void removeTaskTags( List<String> tags ) {
		this.tags = removeListFromList( tags , this.tags );
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
	
	public void addParticipantGroup( String distinguishedName ) {
		this.participantGroupList = addStringToList( distinguishedName , this.participantGroupList );
	}
	public void addParticipantGroup( List<String> distinguishedNames ) {
		this.participantGroupList = addListToList( distinguishedNames , this.participantGroupList );
	}
	
	public void removeParticipantGroup( String distinguishedName ) {
		this.participantGroupList = removeStringFromList( distinguishedName , this.participantGroupList );
	}
	
	public void removeParticipantGroup( List<String> distinguishedNames ) {
		this.participantGroupList = removeListFromList( distinguishedNames , this.participantGroupList );
	}
	
	public void addParticipantUnit( String distinguishedName ) {
		this.participantUnitList = addStringToList( distinguishedName , this.participantUnitList );
	}
	public void addParticipantUnit( List<String> distinguishedNames ) {
		this.participantUnitList = addListToList( distinguishedNames , this.participantUnitList );
	}
	
	public void removeParticipantUnit( String distinguishedName ) {
		this.participantUnitList = removeStringFromList( distinguishedName , this.participantUnitList );
	}
	
	public void removeParticipantUnit( List<String> distinguishedNames ) {
		this.participantUnitList = removeListFromList( distinguishedNames , this.participantUnitList );
	}
	
	public void addParticipantPerson( String distinguishedName ) {
		this.participantPersonList = addStringToList( distinguishedName , this.participantPersonList );
	}
	
	public void addParticipantPerson( List<String> distinguishedNames ) {
		this.participantPersonList = addListToList( distinguishedNames , this.participantPersonList );
	}
	
	public void removeParticipantPerson( String distinguishedName ) {
		this.participantPersonList = removeStringFromList( distinguishedName , this.participantPersonList );
	}
	
	public void removeParticipantPerson( List<String> distinguishedNames ) {
		this.participantPersonList = removeListFromList( distinguishedNames , this.participantPersonList );
	}
	
	public void addParticipantIdentity( String distinguishedName ) {
		this.participantIdentityList = addStringToList( distinguishedName , this.participantIdentityList );
	}
	
	public void addParticipantIdentity( List<String> distinguishedNames ) {
		this.participantIdentityList = addListToList( distinguishedNames , this.participantIdentityList );
	}
	
	public void removeParticipantIdentity( String distinguishedName ) {
		this.participantIdentityList = removeStringFromList( distinguishedName , this.participantIdentityList );
	}
	
	public void removeParticipantIdentity( List<String> distinguishedNames ) {
		this.participantIdentityList = removeListFromList( distinguishedNames , this.participantIdentityList );
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
}