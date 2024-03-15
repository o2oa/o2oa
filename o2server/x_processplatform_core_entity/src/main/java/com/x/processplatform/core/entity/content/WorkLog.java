package com.x.processplatform.core.entity.content;

import java.lang.reflect.InvocationTargetException;
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
import javax.persistence.PostLoad;
import javax.persistence.Table;
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
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.processplatform.core.entity.PersistenceProperties;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.ticket.Tickets;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "WorkLog", description = "流程平台工作日志.")
@Entity
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
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

	@FieldDescribe("工作日志标识.")
	@Schema(description = "工作日志标识.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	// 以上为 JpaObject 默认字段

	public void onPersist() throws Exception {
		// nothing
	}

	@PostLoad
	public void postLoad() {
		if (null != this.properties) {
			this.splitValueList = this.properties.getSplitValueList();
			this.splitTokenList = this.properties.getSplitTokenList();
			this.goBackFromActivityType = this.properties.getGoBackFromActivityType();
			this.goBackFromActivity = this.properties.getGoBackFromActivity();
			this.goBackFromActivityToken = this.properties.getGoBackFromActivityToken();
			this.tickets = this.properties.getTickets();
		}
	}

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
		o.setSplitValueList(work.getSplitValueList());
		o.setFromTime(date);
		o.setCompleted(false);
		o.setConnected(false);
		return o;
	}

	public WorkLog() {
		this.properties = new WorkLogProperties();
	}

	public WorkLog(WorkLog workLog) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		this();
		WorkLog copy = XGsonBuilder.convert(workLog, WorkLog.class);
		copy.copyTo(this, JpaObject.id_FIELDNAME);
	}

	public static final String SPLITVALUELIST_FIELDNAME = "splitValueList";
	@Transient
	@FieldDescribe("拆分值列表.")
	@Schema(description = "拆分值列表.")
	private List<String> splitValueList;

	public List<String> getSplitValueList() {
		if ((null == this.splitValueList) && (null != this.properties)) {
			this.splitValueList = this.properties.getSplitValueList();
		}
		return this.splitValueList;
	}

	public void setSplitValueList(List<String> splitValueList) {
		this.getProperties().setSplitValueList(splitValueList);
		this.splitValueList = splitValueList;
	}

	public static final String SPLITTOKENLIST_FIELDNAME = "splitTokenList";
	@Transient
	@FieldDescribe("拆分标识列表.")
	@Schema(description = "拆分标识列表.")
	private List<String> splitTokenList;

	public List<String> getSplitTokenList() {
		return this.splitTokenList;
	}

	public void setSplitTokenList(List<String> splitTokenList) {
		this.splitTokenList = splitTokenList;
		this.getProperties().setSplitTokenList(splitTokenList);
	}

	public static final String GOBACKFROMACTIVITYTYPE_FIELDNAME = "goBackFromActivityType";
	@Transient
	@FieldDescribe("退回发起活动环节类型.")
	private ActivityType goBackFromActivityType;

	public ActivityType getGoBackFromActivityType() {
		if ((null == this.goBackFromActivityType) && (null != this.properties)) {
			this.goBackFromActivityType = this.properties.getGoBackFromActivityType();
		}
		return goBackFromActivityType;
	}

	public void setGoBackFromActivityType(ActivityType goBackFromActivityType) {
		this.getProperties().setGoBackFromActivityType(goBackFromActivityType);
		this.goBackFromActivityType = goBackFromActivityType;
	}

	public static final String GOBACKFROMACTIVITY_FIELDNAME = "goBackFromActivity";
	@Transient
	@FieldDescribe("退回发起活动环节标识.")
	private String goBackFromActivity;

	public String getGoBackFromActivity() {
		if ((null == this.goBackFromActivity) && (null != this.properties)) {
			this.goBackFromActivity = this.properties.getGoBackFromActivity();
		}
		return goBackFromActivity;
	}

	public void setGoBackFromActivity(String goBackFromActivity) {
		this.getProperties().setGoBackFromActivity(goBackFromActivity);
		this.goBackFromActivity = goBackFromActivity;
	}

	public static final String GOBACKFROMACTIVITYTOKEN_FIELDNAME = "goBackFromActivityToken";
	@Transient
	@FieldDescribe("退回发起活动环节令牌.")
	private String goBackFromActivityToken;

	public String getGoBackFromActivityToken() {
		if ((null == this.goBackFromActivityToken) && (null != this.properties)) {
			this.goBackFromActivityToken = this.properties.getGoBackFromActivityToken();
		}
		return goBackFromActivityToken;
	}

	public void setGoBackFromActivityToken(String goBackFromActivityToken) {
		this.getProperties().setGoBackFromActivityToken(goBackFromActivityToken);
		this.goBackFromActivityToken = goBackFromActivityToken;
	}

	public static final String TICKETS_FIELDNAME = "tickets";
	@Transient
	@FieldDescribe("退回发起活动环节令牌.")
	private Tickets tickets;

	public Tickets getTickets() {
		if ((null == this.tickets) && (null != this.properties)) {
			this.tickets = this.properties.getTickets();
		}
		return tickets;
	}

	public void setTickets(Tickets tickets) {
		this.getProperties().setTickets(tickets);
		this.tickets = tickets;
	}

	public WorkLogProperties getProperties() {
		if (null == this.properties) {
			this.properties = new WorkLogProperties();
		}
		return this.properties;
	}

	public void setProperties(WorkLogProperties properties) {
		this.properties = properties;
	}

	public static final String JOB_FIELDNAME = "job";
	@FieldDescribe("任务标识.")
	@Schema(description = "任务标识.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + JOB_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + JOB_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String job;

	public static final String WORK_FIELDNAME = "work";
	@FieldDescribe("工作标识.")
	@Schema(description = "工作标识.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + WORK_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + WORK_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String work;

	public static final String WORKCOMPLETED_FIELDNAME = "workCompleted";
	@FieldDescribe("已完成工作标识.")
	@Schema(description = "已完成工作标识.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + WORKCOMPLETED_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + WORKCOMPLETED_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workCompleted;

	public static final String COMPLETED_FIELDNAME = "completed";
	// 必填
	@FieldDescribe("工作是否已经完成.")
	@Schema(description = "工作是否已经完成.")
	@Column(name = ColumnNamePrefix + COMPLETED_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean completed;

	public static final String FROMACTIVITY_FIELDNAME = "fromActivity";
	@FieldDescribe("开始活动标识.")
	@Schema(description = "开始活动标识.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + FROMACTIVITY_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + FROMACTIVITY_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String fromActivity;

	public static final String FROMACTIVITYTYPE_FIELDNAME = "fromActivityType";
	@FieldDescribe("开始活动类型.")
	@Index(name = TABLE + IndexNameMiddle + FROMACTIVITYTYPE_FIELDNAME)
	@Enumerated(EnumType.STRING)
	@Column(length = ActivityType.length, name = ColumnNamePrefix + FROMACTIVITYTYPE_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private ActivityType fromActivityType;

	public static final String FROMACTIVITYNAME_FIELDNAME = "fromActivityName";
	@FieldDescribe("开始活动名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ FROMACTIVITYNAME_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fromActivityName;

	public static final String FROMACTIVITYALIAS_FIELDNAME = "fromActivityAlias";
	@FieldDescribe("开始活动别名.")
	@Column(length = length_255B, name = ColumnNamePrefix + FROMACTIVITYALIAS_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fromActivityAlias;

	public static final String FROMACTIVITYTOKEN_FIELDNAME = "fromActivityToken";
	@FieldDescribe("开始节点Token")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + FROMACTIVITYTOKEN_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + FROMACTIVITYTOKEN_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String fromActivityToken;

	public static final String FROMGROUP_FIELDNAME = "fromGroup";
	@FieldDescribe("开始分组")
	@CheckPersist(allowEmpty = true)
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + FROMGROUP_FIELDNAME)
	private String fromGroup;

	public static final String FROMOPINIONGROUP_FIELDNAME = "fromOpinionGroup";
	@FieldDescribe("开始意见分组")
	@CheckPersist(allowEmpty = true)
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + FROMOPINIONGROUP_FIELDNAME)
	private String fromOpinionGroup;

	public static final String FROMTIME_FIELDNAME = "fromTime";
	@FieldDescribe("开始时间.")
	@Column(name = ColumnNamePrefix + FROMTIME_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + FROMTIME_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date fromTime;

	public static final String ARRIVEDACTIVITY_FIELDNAME = "arrivedActivity";
	@FieldDescribe("结束活动Id，可能为空，如果是未Connected的流程记录")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + ARRIVEDACTIVITY_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + ARRIVEDACTIVITY_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String arrivedActivity;

	public static final String ARRIVEDACTIVITYTYPE_FIELDNAME = "arrivedActivityType";
	@FieldDescribe("结束活动类型.")
	@Column(length = ActivityType.length, name = ColumnNamePrefix + ARRIVEDACTIVITYTYPE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + ARRIVEDACTIVITYTYPE_FIELDNAME)
	@Enumerated(EnumType.STRING)
	@CheckPersist(allowEmpty = true)
	private ActivityType arrivedActivityType;

	public static final String ARRIVEDACTIVITYNAME_FIELDNAME = "arrivedActivityName";
	@FieldDescribe("结束活动名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ ARRIVEDACTIVITYNAME_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String arrivedActivityName;

	public static final String ARRIVEDACTIVITYALIAS_FIELDNAME = "arrivedActivityAlias";
	@FieldDescribe("结束活动别名.")
	@Column(length = length_255B, name = ColumnNamePrefix + ARRIVEDACTIVITYALIAS_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String arrivedActivityAlias;

	public static final String ARRIVEDACTIVITYTOKEN_FIELDNAME = "arrivedActivityToken";
	@FieldDescribe("结束活动Token.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + ARRIVEDACTIVITYTOKEN_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + ARRIVEDACTIVITYTOKEN_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String arrivedActivityToken;

	public static final String ARRIVEDGROUP_FIELDNAME = "arrivedGroup";
	@FieldDescribe("到达分组")
	@CheckPersist(allowEmpty = true)
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + ARRIVEDGROUP_FIELDNAME)
	private String arrivedGroup;

	public static final String ARRIVEDOPINIONGROUP_FIELDNAME = "arrivedOpinionGroup";
	@FieldDescribe("到达意见分组")
	@CheckPersist(allowEmpty = true)
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + ARRIVEDOPINIONGROUP_FIELDNAME)
	private String arrivedOpinionGroup;

	public static final String ARRIVEDTIME_FIELDNAME = "arrivedTime";
	@FieldDescribe("完成时间.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + ARRIVEDTIME_FIELDNAME)
	private Date arrivedTime;

	public static final String APPLICATION_FIELDNAME = "application";
	@FieldDescribe("应用.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + APPLICATION_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String application;

	public static final String APPLICATIONNAME_FIELDNAME = "applicationName";
	@FieldDescribe("应用名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ APPLICATIONNAME_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String applicationName;

	public static final String APPLICATIONALIAS_FIELDNAME = "applicationAlias";
	@FieldDescribe("应用别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ APPLICATIONALIAS_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String applicationAlias;

	public static final String PROCESS_FIELDNAME = "process";
	@FieldDescribe("流程ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + PROCESS_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String process;

	public static final String PROCESSNAME_FIELDNAME = "processName";
	@FieldDescribe("流程名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ PROCESSNAME_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processName;

	public static final String PROCESSALIAS_FIELDNAME = "processAlias";
	@FieldDescribe("流程别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ PROCESSALIAS_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processAlias;

	public static final String ROUTE_FIELDNAME = "route";
	@FieldDescribe("到达节点使用的route ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + ROUTE_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String route;

	public static final String ROUTENAME_FIELDNAME = "routeName";
	@FieldDescribe("到达节点使用Route Name.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ ROUTENAME_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + ROUTENAME_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String routeName;

	public static final String CONNECTED_FIELDNAME = "connected";
	@FieldDescribe("是否已经完整填写了From和Arrived.")
	@Column(name = ColumnNamePrefix + CONNECTED_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean connected;

	public static final String DURATION_FIELDNAME = "duration";
	@FieldDescribe("工作时长(分钟数).")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + DURATION_FIELDNAME)
	private Long duration;

	// 不需要索引
	public static final String SPLITTING_FIELDNAME = "splitting";
	@FieldDescribe("是否是拆分中的工作,用于回溯时候将值改回去。")
	@Column(name = ColumnNamePrefix + SPLITTING_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean splitting;

	// 不需要索引
	public static final String SPLITTOKEN_FIELDNAME = "splitToken";
	@FieldDescribe("拆分工作令牌,用于回溯时候将值改回去。")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + SPLITTOKEN_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String splitToken;

	// 不需要索引
	public static final String SPLITVALUE_FIELDNAME = "splitValue";
	@FieldDescribe("拆分值,用于回溯时候将值改回去。")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + SPLITVALUE_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String splitValue;

	public static final String SPLITWORK_FIELDNAME = "splitWork";
	@FieldDescribe("拆分自工作")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + SPLITWORK_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + SPLITWORK_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String splitWork;

	public static final String TYPE_FIELDNAME = "type";
	@FieldDescribe("类型,与ProcessingAttributes的type对应.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + TYPE_FIELDNAME)
	@Index(name = TABLE + TYPE_FIELDNAME + SPLITWORK_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String type;

	public static final String PROPERTIES_FIELDNAME = "properties";
	@FieldDescribe("属性对象存储字段.")
	@Persistent(fetch = FetchType.EAGER)
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + PROPERTIES_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private WorkLogProperties properties;

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
		return ARRIVEDOPINIONGROUP_FIELDNAME;
	}

}