package com.x.processplatform.core.entity.content;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.PersistenceProperties;
import com.x.processplatform.core.entity.element.ActivityType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Snap", description = "流程平台快照.")
@Entity
@ContainerEntity(dumpSize = 2, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Content.Snap.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Content.Snap.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Snap extends SliceJpaObject {

	private static final long serialVersionUID = -1821792646042264572L;

	private static final String TABLE = PersistenceProperties.Content.Snap.table;

	public static final String TYPE_SNAP = "snap";

	public static final String TYPE_SUSPEND = "suspend";

	public static final String TYPE_ABANDONED = "abandoned";

	public static final String TYPE_ABANDONEDWORKCOMPLETED = "abandonedWorkCompleted";

	public static final String TYPE_SNAPWORKCOMPLETED = "snapWorkCompleted";

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

	// 更新运行方法
	public void onPersist() throws Exception {
		if (StringTools.utf8Length(this.getProperties().getTitle()) > length_255B) {
			this.title = StringTools.utf8SubString(this.getProperties().getTitle(), length_255B - 3) + "...";
		}
	}

	@PostLoad
	public void postLoad() {
		if ((null != this.properties) && StringUtils.isNotEmpty(this.getProperties().getTitle())) {
			this.title = this.getProperties().getTitle();
		}
	}

	public void setTitle(String title) {
		this.title = title;
		this.getProperties().setTitle(title);
	}

	public String getTitle() {
		if ((null != this.properties) && StringUtils.isNotEmpty(this.properties.getTitle())) {
			return this.properties.getTitle();
		} else {
			return this.title;
		}
	}

	public Snap() {
		// nothing
	}

	public Snap(Work work) {
		this.setTitle(work.getTitle());
		this.setJob(work.getJob());
		this.setApplication(work.getApplication());
		this.setApplicationName(work.getApplicationName());
		this.setApplicationAlias(work.getApplicationAlias());
		this.setProcess(work.getProcess());
		this.setProcessName(work.getProcessName());
		this.setProcessAlias(work.getProcessAlias());
		this.setCreatorIdentity(work.getCreatorIdentity());
		this.setCreatorPerson(work.getCreatorPerson());
		this.setCreatorUnit(work.getCreatorUnit());
		this.setActivity(work.getActivity());
		this.setActivityAlias(work.getActivityAlias());
		this.setActivityDescription(work.getActivityDescription());
		this.setActivityName(work.getActivityName());
		this.setActivityToken(work.getActivityToken());
		this.setActivityType(work.getActivityType());
		this.setStartTime(work.getStartTime());
	}

	public Snap(WorkCompleted workCompleted) {
		this.setTitle(workCompleted.getTitle());
		this.setJob(workCompleted.getJob());
		this.setApplication(workCompleted.getApplication());
		this.setApplicationName(workCompleted.getApplicationName());
		this.setApplicationAlias(workCompleted.getApplicationAlias());
		this.setProcess(workCompleted.getProcess());
		this.setProcessName(workCompleted.getProcessName());
		this.setProcessAlias(workCompleted.getProcessAlias());
		this.setCreatorIdentity(workCompleted.getCreatorIdentity());
		this.setCreatorPerson(workCompleted.getCreatorPerson());
		this.setCreatorUnit(workCompleted.getCreatorUnit());
		this.setStartTime(workCompleted.getStartTime());
	}

	public SnapProperties getProperties() {
		if (null == this.properties) {
			this.properties = new SnapProperties();
		}
		return this.properties;
	}

	public void setProperties(SnapProperties properties) {
		this.properties = properties;
	}

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("类型.")
	@Column(length = length_32B, name = ColumnNamePrefix + type_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + type_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String type;

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("快照创建人.")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String person;

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("标题.")
	@Column(length = length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title;

	public static final String job_FIELDNAME = "job";
	@FieldDescribe("任务.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + job_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + job_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String job;

	public static final String application_FIELDNAME = "application";
	@FieldDescribe("应用.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + application_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String application;

	public static final String applicationName_FIELDNAME = "applicationName";
	@FieldDescribe("应用名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + applicationName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + applicationName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String applicationName;

	public static final String applicationAlias_FIELDNAME = "applicationAlias";
	@FieldDescribe("应用别名.")
	@Column(length = length_255B, name = ColumnNamePrefix + applicationAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String applicationAlias;

	public static final String process_FIELDNAME = "process";
	@FieldDescribe("流程ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + process_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + process_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String process;

	public static final String processName_FIELDNAME = "processName";
	@FieldDescribe("流程名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + processName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + processName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processName;

	public static final String processAlias_FIELDNAME = "processAlias";
	@FieldDescribe("流程别名.")
	@Column(length = length_255B, name = ColumnNamePrefix + processAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processAlias;

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("创建人，可能为空，如果由系统创建.")
	@Column(length = length_255B, name = ColumnNamePrefix + creatorPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorPerson_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;

	public static final String creatorIdentity_FIELDNAME = "creatorIdentity";
	@FieldDescribe("创建人Identity,可能为空,如果由系统创建.")
	@Column(length = length_255B, name = ColumnNamePrefix + creatorIdentity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity;

	public static final String creatorUnit_FIELDNAME = "creatorUnit";
	@FieldDescribe("创建人组织,可能为空,如果由系统创建。")
	@Column(length = length_255B, name = ColumnNamePrefix + creatorUnit_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorUnit_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorUnit;

	public static final String activity_FIELDNAME = "activity";
	@FieldDescribe("活动ID,针对已完成工作添加待阅字段为空.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + activity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activity;

	public static final String activityName_FIELDNAME = "activityName";
	@FieldDescribe("活动名称,针对已完成工作添加待阅字段为空.")
	@Column(length = length_255B, name = ColumnNamePrefix + activityName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityName;

	public static final String activityAlias_FIELDNAME = "activityAlias";
	@FieldDescribe("活动别名,针对已完成工作添加待阅字段为空.")
	@Column(length = length_255B, name = ColumnNamePrefix + activityAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityAlias;

	public static final String activityDescription_FIELDNAME = "activityDescription";
	@FieldDescribe("活动说明,针对已完成工作添加待阅字段为空.")
	@Column(length = length_255B, name = ColumnNamePrefix + activityDescription_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityDescription;

	public static final String activityType_FIELDNAME = "activityType";
	@FieldDescribe("活动类型,针对已完成工作添加待阅字段为空.")
	@Enumerated(EnumType.STRING)
	@Column(length = ActivityType.length, name = ColumnNamePrefix + activityType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private ActivityType activityType;

	public static final String activityToken_FIELDNAME = "activityToken";
	@FieldDescribe("活动Token,针对已完成工作添加待阅字段为空.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + activityToken_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityToken;

	public static final String startTime_FIELDNAME = "startTime";
	@FieldDescribe("流程启动时间.")
	@Column(name = ColumnNamePrefix + startTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + startTime_FIELDNAME)
	private Date startTime;

	public static final String properties_FIELDNAME = "properties";
	@FieldDescribe("属性对象存储字段.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private SnapProperties properties;

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getProcessAlias() {
		return processAlias;
	}

	public void setProcessAlias(String processAlias) {
		this.processAlias = processAlias;
	}

	public String getApplicationAlias() {
		return applicationAlias;
	}

	public void setApplicationAlias(String applicationAlias) {
		this.applicationAlias = applicationAlias;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public String getCreatorIdentity() {
		return creatorIdentity;
	}

	public void setCreatorIdentity(String creatorIdentity) {
		this.creatorIdentity = creatorIdentity;
	}

	public String getCreatorUnit() {
		return creatorUnit;
	}

	public void setCreatorUnit(String creatorUnit) {
		this.creatorUnit = creatorUnit;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getActivityAlias() {
		return activityAlias;
	}

	public void setActivityAlias(String activityAlias) {
		this.activityAlias = activityAlias;
	}

	public String getActivityDescription() {
		return activityDescription;
	}

	public void setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public String getActivityToken() {
		return activityToken;
	}

	public void setActivityToken(String activityToken) {
		this.activityToken = activityToken;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

}
