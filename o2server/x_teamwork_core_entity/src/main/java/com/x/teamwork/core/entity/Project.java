package com.x.teamwork.core.entity;

import java.util.ArrayList;
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

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Project.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Project.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Project extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.Project.table;

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
	@FieldDescribe("项目名称")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + title_FIELDNAME)
	private String title;

	public static final String order_FIELDNAME = "order";
	@FieldDescribe("排序号")
	@Column( name = ColumnNamePrefix + order_FIELDNAME )
	private Integer order;
	
	public static final String type_FIELDNAME = "type";
	@FieldDescribe("项目类型：普通项目 | 软件项目")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + type_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + type_FIELDNAME)
	@CheckPersist(allowEmpty = true )
	private String type = "普通项目";
	
	public static final String icon_FIELDNAME = "icon";
	@FieldDescribe("图标文件ID")
	@Column( length = JpaObject.length_id, name = ColumnNamePrefix + icon_FIELDNAME)
	@CheckPersist(allowEmpty = true )
	private String icon = null;
	
	public static final String progress_FIELDNAME = "progress";
	@FieldDescribe("总体进度：记录4位数，显示的时候除以100")
	@Column( name = ColumnNamePrefix + progress_FIELDNAME)
	private Integer progress = 0;
	
	public static final String groupCount_FIELDNAME = "groupCount";
	@FieldDescribe("分组个数，未分组为0")
	@Column( name = ColumnNamePrefix + groupCount_FIELDNAME)
	private Integer groupCount = 0;
	
	public static final String deleted_FIELDNAME = "deleted";
	@FieldDescribe("是否已经删除")
	@Column( name = ColumnNamePrefix + deleted_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + deleted_FIELDNAME )
	private Boolean deleted = false;
	
	public static final String completed_FIELDNAME = "completed";
	@FieldDescribe("是否已经完成")
	@Column( name = ColumnNamePrefix + completed_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + completed_FIELDNAME )
	private Boolean completed = false;
	
	public static final String archive_FIELDNAME = "archive";
	@FieldDescribe("是否已经归档")
	@Column( name = ColumnNamePrefix + archive_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + archive_FIELDNAME )
	private Boolean archive = false;
	
	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("创建者，可能为System，如果由系统创建。")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + creatorPerson_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + creatorPerson_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;
	
	public static final String executor_FIELDNAME = "executor";
	@FieldDescribe("执行者|负责人")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + executor_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + executor_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String executor;

	public static final String starPersonList_FIELDNAME = "starPersonList";
	@FieldDescribe("标星人员")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + starPersonList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + starPersonList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + starPersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + starPersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> starPersonList;
	
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

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
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

	public List<String> getParticipantUnitList() {
		return participantUnitList;
	}

	public void setParticipantUnitList(List<String> participantUnitList) {
		this.participantUnitList = participantUnitList;
	}

	public List<String> getParticipantGroupList() {
		return participantGroupList;
	}

	public void setParticipantGroupList(List<String> participantGroupList) {
		this.participantGroupList = participantGroupList;
	}

	public List<String> getManageablePersonList() {
		return manageablePersonList;
	}

	public void setManageablePersonList(List<String> manageablePersonList) {
		this.manageablePersonList = manageablePersonList;
	}
	
	public List<String> getParticipantIdentityList() {
		return participantIdentityList;
	}

	public void setParticipantIdentityList(List<String> participantIdentityList) {
		this.participantIdentityList = participantIdentityList;
	}
	
	public List<String> getStarPersonList() {
		return starPersonList;
	}

	public void setStarPersonList(List<String> starPersonList) {
		this.starPersonList = starPersonList;
	}	
	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	public Boolean getArchive() {
		return archive;
	}

	public void setArchive(Boolean archive) {
		this.archive = archive;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void addStarPerson( String distinguishedName ) {
		this.starPersonList = addStringToList( distinguishedName , this.starPersonList );
	}
	public void addStarPersons( List<String> distinguishedNames ) {
		this.starPersonList = addListToList( distinguishedNames , this.starPersonList );
	}
	public void removeStarPerson( String distinguishedName ) {
		this.starPersonList = removeStringFromList( distinguishedName , this.starPersonList );
	}
	public void removeStarPersons( List<String> distinguishedNames ) {
		this.starPersonList = removeListFromList( distinguishedNames , this.starPersonList );
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
	public void removeManageablePersons( List<String> distinguishedNames ) {
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
	public void removeParticipantGroups( List<String> distinguishedNames ) {
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
	public void removeParticipantUnits( List<String> distinguishedNames ) {
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
	public void removeParticipantPersons( List<String> distinguishedNames ) {
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
	public void removeParticipantIdentitys( List<String> distinguishedNames ) {
		this.participantIdentityList = removeListFromList( distinguishedNames , this.participantIdentityList );
	}
	
	public Integer getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(Integer groupCount) {
		this.groupCount = groupCount;
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
}