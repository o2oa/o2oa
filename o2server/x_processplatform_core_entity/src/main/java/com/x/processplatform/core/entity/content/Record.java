package com.x.processplatform.core.entity.content;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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

	/* 正常流转 */
	public static final String TYPE_CURRENTTASK = "currentTask";

	/* 正常流转 */
	public static final String TYPE_TASK = "task";

	/* 转交流转 */
	public static final String TYPE_APPENDTASK = "appendTask";

	/* 回退流转 */
	public static final String TYPE_BACK = "back";

	/* 调度 */
	public static final String TYPE_REROUTE = "reroute";

	/* 撤回 */
	public static final String TYPE_RETRACT = "retract";

	/* 回滚 */
	public static final String TYPE_ROLLBACK = "rollback";

	/* 重置 */
	public static final String TYPE_RESET = "reset";

	/* 增加分支 */
	public static final String TYPE_ADDSPLIT = "addSplit";

	/* 催办 */
	public static final String TYPE_URGE = "urge";

	/* 超时 */
	public static final String TYPE_EXPIRE = "expire";

	/* 待阅 */
	public static final String TYPE_READ = "read";

	/* 授权 */
	public static final String TYPE_EMPOWER = "empower";

	/* 超时自动流转 */
	public static final String TYPE_PASSEXPIRED = "passExpired";

	/* 外部调用流转 */
	public static final String TYPE_SERVICE = "service";
	
	/* 添加待办 */
	public static final String TYPE_TASKADD = "taskAdd";

	/* 定制意见 */
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
	/* 更新运行方法 */

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
		this.getProperties().setRouteName(task.getRouteName());
		this.getProperties().setMediaOpinion(task.getMediaOpinion());
		this.getProperties().setStartTime(task.getStartTime());
		this.getProperties().setEmpowerFromIdentity(task.getEmpowerFromIdentity());
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
		this.getProperties().setStartTime(workLog.getFromTime());
		this.getProperties().setFromGroup(workLog.getFromGroup());
		this.getProperties().setFromOpinionGroup(workLog.getFromOpinionGroup());
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

	public static final String arrivedActivity_FIELDNAME = "arrivedActivity";
	@FieldDescribe("结束活动Id，可能为空，如果是未Connected的流程记录")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + arrivedActivity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + arrivedActivity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String arrivedActivity;

	public static final String arrivedActivityType_FIELDNAME = "arrivedActivityType";
	@FieldDescribe("结束活动类型.")
	@Column(length = ActivityType.length, name = ColumnNamePrefix + arrivedActivityType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + arrivedActivityType_FIELDNAME)
	@Enumerated(EnumType.STRING)
	@CheckPersist(allowEmpty = true)
	private ActivityType arrivedActivityType;

	public static final String arrivedActivityName_FIELDNAME = "arrivedActivityName";
	@FieldDescribe("结束活动名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ arrivedActivityName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + arrivedActivityName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String arrivedActivityName;

	public static final String arrivedActivityAlias_FIELDNAME = "arrivedActivityAlias";
	@FieldDescribe("结束活动名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + arrivedActivityAlias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + arrivedActivityAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String arrivedActivityAlias;

	public static final String arrivedActivityToken_FIELDNAME = "arrivedActivityToken";
	@FieldDescribe("结束活动Token.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + arrivedActivityToken_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + arrivedActivityToken_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String arrivedActivityToken;

	public static final String recordTime_FIELDNAME = "recordTime";
	@FieldDescribe("记录时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + recordTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + recordTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date recordTime;

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("记录人员")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String identity_FIELDNAME = "identity";
	@FieldDescribe("记录身份")
	@Column(length = length_255B, name = ColumnNamePrefix + identity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + identity_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String identity;

	public static final String unit_FIELDNAME = "unit";
	@FieldDescribe("记录人员所在组织.")
	@Column(length = length_255B, name = ColumnNamePrefix + unit_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + unit_FIELDNAME)
	@CheckPersist(allowEmpty = false)
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

	public String getArrivedActivity() {
		return arrivedActivity;
	}

	public void setArrivedActivity(String arrivedActivity) {
		this.arrivedActivity = arrivedActivity;
	}

	public ActivityType getArrivedActivityType() {
		return arrivedActivityType;
	}

	public void setArrivedActivityType(ActivityType arrivedActivityType) {
		this.arrivedActivityType = arrivedActivityType;
	}

	public String getArrivedActivityName() {
		return arrivedActivityName;
	}

	public void setArrivedActivityName(String arrivedActivityName) {
		this.arrivedActivityName = arrivedActivityName;
	}

	public String getArrivedActivityAlias() {
		return arrivedActivityAlias;
	}

	public void setArrivedActivityAlias(String arrivedActivityAlias) {
		this.arrivedActivityAlias = arrivedActivityAlias;
	}

	public String getArrivedActivityToken() {
		return arrivedActivityToken;
	}

	public void setArrivedActivityToken(String arrivedActivityToken) {
		this.arrivedActivityToken = arrivedActivityToken;
	}

}