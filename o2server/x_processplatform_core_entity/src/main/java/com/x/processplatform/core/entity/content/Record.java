package com.x.processplatform.core.entity.content;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.PersistenceProperties;
import com.x.processplatform.core.entity.content.RecordProperties.NextManual;
import com.x.processplatform.core.entity.element.ActivityType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Record", description = "流程平台记录.")
@Entity
@ContainerEntity(dumpSize = 100, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Content.Record.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Content.Record.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Record extends SliceJpaObject {

	private static final long serialVersionUID = 8673378766635237050L;

	// 正常流转
	public static final String TYPE_CURRENTTASK = "currentTask";

	// 正常流转
	public static final String TYPE_TASK = "task";

	// 转交流转
	public static final String TYPE_APPENDTASK = "appendTask";

	// 调度
	public static final String TYPE_REROUTE = "reroute";

	// 撤回
	public static final String TYPE_RETRACT = "retract";

	// 回滚
	public static final String TYPE_ROLLBACK = "rollback";

	// 重置
	public static final String TYPE_RESET = "reset";

	// 增加分支
	public static final String TYPE_ADDSPLIT = "addSplit";

	// 催办
	public static final String TYPE_URGE = "urge";

	// 超时
	public static final String TYPE_EXPIRE = "expire";

	// 待阅
	public static final String TYPE_READ = "read";

	// 授权
	public static final String TYPE_EMPOWER = "empower";

	// 超时自动流转
	public static final String TYPE_PASSEXPIRED = "passExpired";

	// 外部调用流转
	public static final String TYPE_SERVICE = "service";

	// 添加待办
	public static final String TYPE_TASKADD = "taskAdd";

	// 退回
	public static final String TYPE_GOBACK = "goBack";

	// 工作触发流转
	public static final String TYPE_WORKTRIGGERPROCESSING = "workTriggerProcessing";

	// 待办触发流转
	public static final String TYPE_TASKTRIGGERPROCESSING = "taskTriggerProcessing";

	// 终止工作
	public static final String TYPE_TERMINATE = "terminate";

	// 定制意见
	public static final String TYPE_CUSTOM = "custom";

	private static final String TABLE = PersistenceProperties.Content.Record.table;

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
		switch (Objects.toString(this.type)) {
		case TYPE_URGE:
		case TYPE_EXPIRE:
			this.display = false;
			break;
		default:
			this.display = true;
		}
	}

	@PostLoad
	public void postLoad() {
		if (null != this.properties) {
			this.routeName = this.properties.getRouteName();
			this.opinion = this.properties.getOpinion();
			this.mediaOpinion = this.properties.getMediaOpinion();
			this.empowerFromIdentity = this.properties.getEmpowerFromIdentity();
			this.nextManualList = this.properties.getNextManualList();
			this.nextManualTaskIdentityList = this.getProperties().getNextManualTaskIdentityList();
			this.startTime = this.getProperties().getStartTime();
		}
	}

	public Record() {
		this.display = true;
		this.recordTime = new Date();
		this.order = recordTime.getTime();
		this.properties = new RecordProperties();

	}

	public Record(WorkLog workLog, Task task) {
		this(workLog);
		this.setIdentity(task.getIdentity());
		this.setPerson(task.getPerson());
		this.setUnit(task.getUnit());
		this.getProperties().setOpinion(task.getOpinion());
		this.setRouteName(task.getRouteName());
		this.setMediaOpinion(task.getMediaOpinion());
		this.setStartTime(task.getStartTime());
		this.setEmpowerFromIdentity(task.getEmpowerFromIdentity());
	}

	public Record(WorkLog workLog) {
		this();
		this.setApplication(workLog.getApplication());
		this.setProcess(workLog.getProcess());
		this.setWork(workLog.getWork());
		this.setWorkCompleted(workLog.getWorkCompleted());
		this.setCompleted(workLog.getCompleted());
		this.setJob(workLog.getJob());
		this.setDisplay(true);
		this.setFromActivity(workLog.getFromActivity());
		this.setFromActivityType(workLog.getFromActivityType());
		this.setFromActivityName(workLog.getFromActivityName());
		this.setFromActivityAlias(workLog.getFromActivityAlias());
		this.setFromActivityToken(workLog.getFromActivityToken());
		this.setStartTime(workLog.getFromTime());
		this.getProperties().setFromGroup(workLog.getFromGroup());
		this.getProperties().setFromOpinionGroup(workLog.getFromOpinionGroup());
	}

	public static final String ROUTENAME_FIELDNAME = "routeName";
	@FieldDescribe("路由名称.")
	@Transient
	private String routeName;

	public String getRouteName() {
		if ((null == this.routeName) && (null != this.properties)) {
			this.routeName = this.properties.getRouteName();
		}
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.getProperties().setRouteName(routeName);
		this.routeName = routeName;
	}

	public static final String OPINION_FIELDNAME = "opinion";
	@Transient
	@FieldDescribe("意见.")
	private String opinion;

	public String getOpinion() {
		if ((null != this.properties) && (null == this.opinion)) {
			this.opinion = this.properties.getOpinion();
		}
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.getProperties().setOpinion(opinion);
		this.opinion = opinion;
	}

	public static final String MEDIAOPINION_FIELDNAME = "mediaOpinion";
	@Transient
	@FieldDescribe("多媒体意见")
	private String mediaOpinion;

	public String getMediaOpinion() {
		if ((null != this.properties) && (null == this.mediaOpinion)) {
			this.mediaOpinion = this.properties.getMediaOpinion();
		}
		return mediaOpinion;
	}

	public void setMediaOpinion(String mediaOpinion) {
		this.getProperties().setMediaOpinion(mediaOpinion);
		this.mediaOpinion = mediaOpinion;
	}

	public static final String EMPOWERFROMIDENTITY_FIELDNAME = "empowerFromIdentity";
	@Transient
	@FieldDescribe("授权自身份")
	private String empowerFromIdentity;

	public String getEmpowerFromIdentity() {
		if ((null != this.properties) && (null == this.empowerFromIdentity)) {
			this.empowerFromIdentity = this.properties.getEmpowerFromIdentity();
		}
		return empowerFromIdentity;
	}

	public void setEmpowerFromIdentity(String empowerFromIdentity) {
		this.getProperties().setEmpowerFromIdentity(empowerFromIdentity);
		this.empowerFromIdentity = empowerFromIdentity;
	}

	public static final String NEXTMANUALLIST_FIELDNAME = "nextManualList";
	@Transient
	@FieldDescribe("后续人工环节")
	private List<NextManual> nextManualList = new ArrayList<>();

	public List<NextManual> getNextManualList() {
		if ((null == nextManualList) && (null != this.properties)) {
			this.nextManualList = this.properties.getNextManualList();
		}
		return nextManualList;
	}

	public void setNextManualList(List<NextManual> nextManualList) {
		this.getProperties().setNextManualList(nextManualList);
		this.nextManualList = nextManualList;
	}

	public static final String NEXTMANUALTASKIDENTITYLIST_FIELDNAME = "nextManualTaskIdentityList";
	@Transient
	@FieldDescribe("后续人工环节处理人")
	private List<String> nextManualTaskIdentityList = new ArrayList<>();

	public List<String> getNextManualTaskIdentityList() {
		if ((null == nextManualTaskIdentityList) && (null != this.properties)) {
			this.nextManualTaskIdentityList = this.properties.getNextManualTaskIdentityList();
		}
		return nextManualTaskIdentityList;
	}

	public void setNextManualTaskIdentityList(List<String> nextManualTaskIdentityList) {
		this.getProperties().setNextManualTaskIdentityList(nextManualTaskIdentityList);
		this.nextManualTaskIdentityList = nextManualTaskIdentityList;
	}

	public static final String STARTTIME_FIELDNAME = "startTime";
	@Transient
	@FieldDescribe("开始时间.")
	private Date startTime;

	public Date getStartTime() {
		if ((null == startTime) && (null != this.properties)) {
			this.startTime = this.properties.getStartTime();
		}
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.getProperties().setStartTime(startTime);
		this.startTime = startTime;
	}

	public RecordProperties getProperties() {
		if (null == this.properties) {
			this.properties = new RecordProperties();
		}
		return this.properties;
	}

	public void setProperties(RecordProperties properties) {
		this.properties = properties;
	}

	public static final String application_FIELDNAME = "application";
	@FieldDescribe("应用.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + application_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String application;

	public static final String process_FIELDNAME = "process";
	@FieldDescribe("流程ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + process_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + process_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String process;

	public static final String job_FIELDNAME = "job";
	@FieldDescribe("任务标识")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + job_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + job_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String job;

	public static final String work_FIELDNAME = "work";
	@FieldDescribe("工作")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + work_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + work_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String work;

	public static final String workCompleted_FIELDNAME = "workCompleted";
	@FieldDescribe("已完成工作")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + workCompleted_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workCompleted;

	public static final String completed_FIELDNAME = "completed";
	@FieldDescribe("工作是否已经完成.")
	/* 必填值 */
	@Column(name = ColumnNamePrefix + completed_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean completed;

	public static final String display_FIELDNAME = "display";
	@FieldDescribe("是否显示.")
	/* 必填值 */
	@Column(name = ColumnNamePrefix + display_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean display;

	public static final String order_FIELDNAME = "order";
	@FieldDescribe("显示排序.")
	/* 必填值 */
	@Column(name = ColumnNamePrefix + order_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + order_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Long order;

	public static final String properties_FIELDNAME = "properties";
	@FieldDescribe("属性对象存储字段.")
	@Persistent(fetch = FetchType.EAGER)
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private RecordProperties properties;

	public static final String fromActivity_FIELDNAME = "fromActivity";
	@FieldDescribe("开始活动Id")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + fromActivity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + fromActivity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fromActivity;

	public static final String fromActivityType_FIELDNAME = "fromActivityType";
	@FieldDescribe("开始活动类型.")
	@Index(name = TABLE + IndexNameMiddle + fromActivityType_FIELDNAME)
	@Enumerated(EnumType.STRING)
	@Column(length = ActivityType.length, name = ColumnNamePrefix + fromActivityType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private ActivityType fromActivityType;

	public static final String fromActivityName_FIELDNAME = "fromActivityName";
	@FieldDescribe("开始活动名称")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ fromActivityName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + fromActivityName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fromActivityName;

	public static final String fromActivityAlias_FIELDNAME = "fromActivityAlias";
	@FieldDescribe("开始活动别名")
	@Column(length = length_255B, name = ColumnNamePrefix + fromActivityAlias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + fromActivityAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fromActivityAlias;

	public static final String fromActivityToken_FIELDNAME = "fromActivityToken";
	@FieldDescribe("开始节点Token")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + fromActivityToken_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + fromActivityToken_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fromActivityToken;

	public static final String recordTime_FIELDNAME = "recordTime";
	@FieldDescribe("记录时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + recordTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + recordTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date recordTime;

	public static final String PERSON_FIELDNAME = "person";
	@FieldDescribe("记录人员")
	@Column(length = length_255B, name = ColumnNamePrefix + PERSON_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + PERSON_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String person;

	public static final String IDENTITY_FIELDNAME = "identity";
	@FieldDescribe("记录身份")
	@Column(length = length_255B, name = ColumnNamePrefix + IDENTITY_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + IDENTITY_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String identity;

	public static final String UNIT_FIELDNAME = "unit";
	@FieldDescribe("记录人员所在组织.")
	@Column(length = length_255B, name = ColumnNamePrefix + UNIT_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + UNIT_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String unit;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("类型.")
	@Column(length = length_64B, name = ColumnNamePrefix + type_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + type_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String type;

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getWorkCompleted() {
		return workCompleted;
	}

	public void setWorkCompleted(String workCompleted) {
		this.workCompleted = workCompleted;
	}

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	public Boolean getDisplay() {
		return display;
	}

	public void setDisplay(Boolean display) {
		this.display = display;
	}

	public Long getOrder() {
		return order;
	}

	public void setOrder(Long order) {
		this.order = order;
	}

	public String getFromActivity() {
		return fromActivity;
	}

	public void setFromActivity(String fromActivity) {
		this.fromActivity = fromActivity;
	}

	public ActivityType getFromActivityType() {
		return fromActivityType;
	}

	public void setFromActivityType(ActivityType fromActivityType) {
		this.fromActivityType = fromActivityType;
	}

	public String getFromActivityName() {
		return fromActivityName;
	}

	public void setFromActivityName(String fromActivityName) {
		this.fromActivityName = fromActivityName;
	}

	public String getFromActivityAlias() {
		return fromActivityAlias;
	}

	public void setFromActivityAlias(String fromActivityAlias) {
		this.fromActivityAlias = fromActivityAlias;
	}

	public String getFromActivityToken() {
		return fromActivityToken;
	}

	public void setFromActivityToken(String fromActivityToken) {
		this.fromActivityToken = fromActivityToken;
	}

	public Date getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(Date recordTime) {
		this.recordTime = recordTime;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

}