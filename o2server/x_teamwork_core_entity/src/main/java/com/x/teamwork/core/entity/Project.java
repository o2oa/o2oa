package com.x.teamwork.core.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.apache.commons.collections4.CollectionUtils;
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

@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Project.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Project.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Project extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.Project.table;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	@Override
	public void onPersist() throws Exception {
	}

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
	@CheckPersist(allowEmpty = true )
	private String type = "普通项目";

	public static final String icon_FIELDNAME = "icon";
	@FieldDescribe("图标文件ID")
	@Column( length = JpaObject.length_id, name = ColumnNamePrefix + icon_FIELDNAME)
	@CheckPersist(allowEmpty = true )
	private String icon = null;

	public static final String progress_FIELDNAME = "progress";
	@FieldDescribe("总体进度：记录0-100，显示的时候添加百分比")
	@Column( name = ColumnNamePrefix + progress_FIELDNAME)
	private Integer progress = 0;

	public static final String groupCount_FIELDNAME = "groupCount";
	@FieldDescribe("分组个数，未分组为0")
	@Column( name = ColumnNamePrefix + groupCount_FIELDNAME)
	private Integer groupCount = 0;

	public static final String deleted_FIELDNAME = "deleted";
	@FieldDescribe("是否已经删除")
	@Column( name = ColumnNamePrefix + deleted_FIELDNAME)
	private Boolean deleted = false;

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

	public static final String templateId_FIELDNAME = "templateId";
	@FieldDescribe("模板id")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + templateId_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + templateId_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String templateId;

	public static final String workStatus_FIELDNAME = "workStatus";
	@FieldDescribe("工作状态：进行中-processing|已完成-completed|已归档-archived|已搁置-delay|已取消(已删除)-canceled")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + workStatus_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private String workStatus = ProjectStatusEnum.PROCESSING.getValue();

	public static final String startTime_FIELDNAME = "startTime";
	@FieldDescribe("项目开始时间")
	@Column( name = ColumnNamePrefix + startTime_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private Date startTime;

	public static final String endTime_FIELDNAME = "endTime";
	@FieldDescribe("项目计划结束时间")
	@Column( name = ColumnNamePrefix + endTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + endTime_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private Date endTime;

	public static final String publishTime_FIELDNAME = "publishTime";
	@FieldDescribe("项目发布时间")
	@Column( name = ColumnNamePrefix + publishTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + publishTime_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private Date publishTime;

	public static final String completedTime_FIELDNAME = "completedTime";
	@FieldDescribe("项目完成时间")
	@Column( name = ColumnNamePrefix + completedTime_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private Date completedTime;

	public static final String source_FIELDNAME = "source";
	@FieldDescribe("项目来源")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + source_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + source_FIELDNAME )
	@CheckPersist(allowEmpty = true )
	private String source;

	public static final String objective_FIELDNAME = "objective";
	@FieldDescribe("项目目标")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column( length = JpaObject.length_1M, name = ColumnNamePrefix + objective_FIELDNAME)
	private String objective;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("项目描述信息")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column( length = JpaObject.length_1M, name = ColumnNamePrefix + description_FIELDNAME)
	private String description;

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

	public static final String participantList_FIELDNAME = "participantList";
	@FieldDescribe("参与人员")
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

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public List<String> getParticipantList() {
		return participantList == null ? Collections.emptyList() : participantList;
	}

	public void setParticipantList(List<String> participantList) {
		this.participantList = participantList;
	}

	public List<String> getStarPersonList() {
		return starPersonList;
	}

	public void setStarPersonList(List<String> starPersonList) {
		this.starPersonList = starPersonList;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getObjective() {
		return objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public Date getCompletedTime() {
		return completedTime;
	}

	public void setCompletedTime(Date completedTime) {
		this.completedTime = completedTime;
	}

	public List<String> getManageablePersonList() {
		return this.manageablePersonList == null ? Collections.emptyList() : this.manageablePersonList;
	}

	public void setManageablePersonList(List<String> manageablePersonList) {
		this.manageablePersonList = manageablePersonList;
	}

	public void addStarPerson(String distinguishedName ) {
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

	public void addParticipantPerson( String distinguishedName ) {
		this.participantList = addStringToList( distinguishedName , this.participantList );
	}
	public void addParticipantPerson( List<String> distinguishedNames ) {
		this.participantList = addListToList( distinguishedNames , this.participantList );
	}
	public void removeParticipantPerson( String distinguishedName ) {
		this.participantList = removeStringFromList( distinguishedName , this.participantList );
	}
	public void removeParticipantPersons( List<String> distinguishedNames ) {
		this.participantList = removeListFromList( distinguishedNames , this.participantList );
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
