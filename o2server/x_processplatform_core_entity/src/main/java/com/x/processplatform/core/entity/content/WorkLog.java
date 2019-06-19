package com.x.processplatform.core.entity.content;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.processplatform.core.entity.PersistenceProperties;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Content.WorkLog.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Content.WorkLog.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class WorkLog extends SliceJpaObject {

	private static final long serialVersionUID = 8673378766635237050L;

	private static final String TABLE = PersistenceProperties.Content.WorkLog.table;

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

	/* 更新运行方法 */

	public static WorkLog createFromWork(Work work, Activity activity, String token, Date date) throws Exception {
		WorkLog o = new WorkLog();
		o.setJob(work.getJob());
		o.setWork(work.getId());
		o.setProcess(work.getProcess());
		o.setProcessName(work.getProcessName());
		o.setApplication(work.getApplication());
		o.setApplicationName(work.getApplicationName());
		o.setFromActivity(activity.getId());
		o.setFromActivityName(activity.getName());
		o.setFromActivityAlias(activity.getAlias());
		o.setFromActivityToken(token);
		o.setFromActivityType(activity.getActivityType());
		o.setFromGroup(activity.getGroup());
		o.setFromOpinionGroup(activity.getOpinionGroup());
		o.setSplitting(work.getSplitting());
		o.setSplitToken(work.getSplitToken());
		o.setSplitValue(work.getSplitValue());
		o.setSplitTokenList(work.getSplitTokenList());
		o.setFromTime(date);
		o.setCompleted(false);
		o.setConnected(false);
		return o;
	}

	public WorkLog() {

	}

	public WorkLog(WorkLog workLog) throws Exception {
		WorkLog copy = XGsonBuilder.convert(workLog, WorkLog.class);
		copy.copyTo(this, JpaObject.id_FIELDNAME);
	}

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
	@Index(name = TABLE + IndexNameMiddle + workCompleted_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workCompleted;

	public static final String completed_FIELDNAME = "completed";
	@FieldDescribe("工作是否已经完成.")
	/* 必填值 */
	@Column(name = ColumnNamePrefix + completed_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + completed_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean completed;

	public static final String fromActivity_FIELDNAME = "fromActivity";
	@FieldDescribe("开始活动Id")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + fromActivity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + fromActivity_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String fromActivity;

	public static final String fromActivityType_FIELDNAME = "fromActivityType";
	@FieldDescribe("开始活动类型.")
	@Index(name = TABLE + IndexNameMiddle + fromActivityType_FIELDNAME)
	@Enumerated(EnumType.STRING)
	@Column(length = ActivityType.length, name = ColumnNamePrefix + fromActivityType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
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
	@CheckPersist(allowEmpty = false)
	private String fromActivityToken;

	public static final String fromGroup_FIELDNAME = "fromGroup";
	@FieldDescribe("开始分组")
	@CheckPersist(allowEmpty = true)
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + fromGroup_FIELDNAME)
	private String fromGroup;

	public static final String fromOpinionGroup_FIELDNAME = "fromOpinionGroup";
	@FieldDescribe("开始意见分组")
	@CheckPersist(allowEmpty = true)
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + fromOpinionGroup_FIELDNAME)
	private String fromOpinionGroup;

	public static final String fromTime_FIELDNAME = "fromTime";
	@FieldDescribe("开始时间.")
	@Column(name = ColumnNamePrefix + fromTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + fromTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date fromTime;

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
	@FieldDescribe("结束活动名称。")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ arrivedActivityName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + arrivedActivityName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String arrivedActivityName;

	public static final String arrivedActivityAlias_FIELDNAME = "arrivedActivityAlias";
	@FieldDescribe("结束活动名称。")
	@Column(length = length_255B, name = ColumnNamePrefix + arrivedActivityAlias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + arrivedActivityAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String arrivedActivityAlias;

	public static final String arrivedActivityToken_FIELDNAME = "arrivedActivityToken";
	@FieldDescribe("开始节点Token")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + arrivedActivityToken_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + arrivedActivityToken_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String arrivedActivityToken;

	public static final String arrivedGroup_FIELDNAME = "arrivedGroup";
	@FieldDescribe("到达分组")
	@CheckPersist(allowEmpty = true)
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + arrivedGroup_FIELDNAME)
	private String arrivedGroup;

	public static final String arrivedOpinionGroup_FIELDNAME = "arrivedOpinionGroup";
	@FieldDescribe("到达意见分组")
	@CheckPersist(allowEmpty = true)
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + arrivedOpinionGroup_FIELDNAME)
	private String arrivedOpinionGroup;

	public static final String arrivedTime_FIELDNAME = "arrivedTime";
	@FieldDescribe("完成时间.")
	@Index(name = TABLE + IndexNameMiddle + arrivedTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + arrivedTime_FIELDNAME)
	private Date arrivedTime;

	public static final String application_FIELDNAME = "application";
	@FieldDescribe("应用.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + application_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String application;

	public static final String applicationName_FIELDNAME = "applicationName";
	@FieldDescribe("应用名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ applicationName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + applicationName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String applicationName;

	public static final String applicationAlias_FIELDNAME = "applicationAlias";
	@FieldDescribe("应用别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ applicationAlias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + applicationAlias_FIELDNAME)
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
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ processName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + processName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processName;

	public static final String processAlias_FIELDNAME = "processAlias";
	@FieldDescribe("流程别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ processAlias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + processAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processAlias;

	public static final String route_FIELDNAME = "route";
	@FieldDescribe("到达节点使用的route ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + route_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + route_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String route;

	public static final String routeName_FIELDNAME = "routeName";
	@FieldDescribe("到达节点使用Route Name.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ routeName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + routeName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String routeName;

	public static final String connected_FIELDNAME = "connected";
	@FieldDescribe("是否已经完整填写了From和Arrived.")
	@Column(name = ColumnNamePrefix + connected_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + connected_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean connected;

	public static final String duration_FIELDNAME = "duration";
	@FieldDescribe("工作时长(分钟数).")
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + duration_FIELDNAME)
	@Column(name = ColumnNamePrefix + duration_FIELDNAME)
	private Long duration;

	/** 不需要索引 */
	public static final String splitting_FIELDNAME = "splitting";
	@FieldDescribe("是否是拆分中的工作,用于回溯时候将值改回去。")
	@Column(name = ColumnNamePrefix + splitting_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean splitting;

	/** 不需要索引 */
	public static final String splitToken_FIELDNAME = "splitToken";
	@FieldDescribe("拆分工作令牌,用于回溯时候将值改回去。")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + splitToken_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String splitToken;

	/** 不需要索引 */
	public static final String splitValue_FIELDNAME = "splitValue";
	@FieldDescribe("拆分值,用于回溯时候将值改回去。")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + splitValue_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String splitValue;

	public static final String splitWork_FIELDNAME = "splitWork";
	@FieldDescribe("拆分自工作")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + splitWork_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + splitWork_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String splitWork;

	/** 不需要索引 */
	public static final String splitTokenList_FIELDNAME = "splitTokenList";
	@FieldDescribe("拆分工作产生的Token")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + splitTokenList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + splitTokenList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_id, name = ColumnNamePrefix + splitTokenList_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private List<String> splitTokenList;

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

	public String getFromActivity() {
		return fromActivity;
	}

	public void setFromActivity(String fromActivity) {
		this.fromActivity = fromActivity;
	}

	public String getFromActivityName() {
		return fromActivityName;
	}

	public void setFromActivityName(String fromActivityName) {
		this.fromActivityName = fromActivityName;
	}

	public String getFromActivityToken() {
		return fromActivityToken;
	}

	public void setFromActivityToken(String fromActivityToken) {
		this.fromActivityToken = fromActivityToken;
	}

	public Date getFromTime() {
		return fromTime;
	}

	public void setFromTime(Date fromTime) {
		this.fromTime = fromTime;
	}

	public String getArrivedActivity() {
		return arrivedActivity;
	}

	public void setArrivedActivity(String arrivedActivity) {
		this.arrivedActivity = arrivedActivity;
	}

	public String getArrivedActivityName() {
		return arrivedActivityName;
	}

	public void setArrivedActivityName(String arrivedActivityName) {
		this.arrivedActivityName = arrivedActivityName;
	}

	public String getArrivedActivityToken() {
		return arrivedActivityToken;
	}

	public void setArrivedActivityToken(String arrivedActivityToken) {
		this.arrivedActivityToken = arrivedActivityToken;
	}

	public Date getArrivedTime() {
		return arrivedTime;
	}

	public void setArrivedTime(Date arrivedTime) {
		this.arrivedTime = arrivedTime;
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

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public Boolean getConnected() {
		return connected;
	}

	public void setConnected(Boolean connected) {
		this.connected = connected;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public ActivityType getFromActivityType() {
		return fromActivityType;
	}

	public void setFromActivityType(ActivityType fromActivityType) {
		this.fromActivityType = fromActivityType;
	}

	public ActivityType getArrivedActivityType() {
		return arrivedActivityType;
	}

	public void setArrivedActivityType(ActivityType arrivedActivityType) {
		this.arrivedActivityType = arrivedActivityType;
	}

	public Boolean getSplitting() {
		return splitting;
	}

	public void setSplitting(Boolean splitting) {
		this.splitting = splitting;
	}

	public String getSplitToken() {
		return splitToken;
	}

	public void setSplitToken(String splitToken) {
		this.splitToken = splitToken;
	}

	public String getSplitValue() {
		return splitValue;
	}

	public void setSplitValue(String splitValue) {
		this.splitValue = splitValue;
	}

	public List<String> getSplitTokenList() {
		return splitTokenList;
	}

	public void setSplitTokenList(List<String> splitTokenList) {
		this.splitTokenList = splitTokenList;
	}

	public String getApplicationAlias() {
		return applicationAlias;
	}

	public void setApplicationAlias(String applicationAlias) {
		this.applicationAlias = applicationAlias;
	}

	public String getProcessAlias() {
		return processAlias;
	}

	public void setProcessAlias(String processAlias) {
		this.processAlias = processAlias;
	}

	public String getFromActivityAlias() {
		return fromActivityAlias;
	}

	public void setFromActivityAlias(String fromActivityAlias) {
		this.fromActivityAlias = fromActivityAlias;
	}

	public String getArrivedActivityAlias() {
		return arrivedActivityAlias;
	}

	public void setArrivedActivityAlias(String arrivedActivityAlias) {
		this.arrivedActivityAlias = arrivedActivityAlias;
	}

	public String getSplitWork() {
		return splitWork;
	}

	public void setSplitWork(String splitWork) {
		this.splitWork = splitWork;
	}

	public String getFromGroup() {
		return fromGroup;
	}

	public void setFromGroup(String fromGroup) {
		this.fromGroup = fromGroup;
	}

	public String getArrivedGroup() {
		return arrivedGroup;
	}

	public void setArrivedGroup(String arrivedGroup) {
		this.arrivedGroup = arrivedGroup;
	}

	public String getFromOpinionGroup() {
		return fromOpinionGroup;
	}

	public void setFromOpinionGroup(String fromOpinionGroup) {
		this.fromOpinionGroup = fromOpinionGroup;
	}

	public String getArrivedOpinionGroup() {
		return arrivedOpinionGroup;
	}

	public void setArrivedOpinionGroup(String arrivedOpinionGroup) {
		this.arrivedOpinionGroup = arrivedOpinionGroup;
	}

	public static String getArrivedopiniongroupFieldname() {
		return arrivedOpinionGroup_FIELDNAME;
	}

}