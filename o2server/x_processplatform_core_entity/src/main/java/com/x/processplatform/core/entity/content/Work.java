package com.x.processplatform.core.entity.content;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.processplatform.ManualTaskIdentityMatrix;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.PersistenceProperties;
import com.x.processplatform.core.entity.content.WorkProperties.GoBackStore;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.ticket.Ticket;
import com.x.processplatform.core.entity.ticket.Tickets;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Work", description = "流程平台工作.")
@Entity
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Content.Work.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Content.Work.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

/**
 * 7.2.0版本 增加manualTaskIdentityMatrix
 * 
 * @author ray
 *
 */
public class Work extends SliceJpaObject implements ProjectionInterface {

	private static final long serialVersionUID = 7668822947307502058L;
	private static final String TABLE = PersistenceProperties.Content.Work.table;
	public static final String TITLEALIAS_SUBJECT = "subject";
	public static final String WORKCREATETYPE_SURFACE = "surface";
	public static final String WORKCREATETYPE_ASSIGN = "assign";

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
		if (StringUtils.isEmpty(this.startTimeMonth) && (null != this.startTime)) {
			this.startTimeMonth = DateTools.format(this.startTime, DateTools.format_yyyyMM);
		}
		this.serial = Objects.toString(this.serial, "");
		// add by Ray 20200622
		if (StringTools.utf8Length(this.getProperties().getTitle()) > length_255B) {
			this.title = StringTools.utf8SubString(this.getProperties().getTitle(), length_255B - 3) + "...";
		}
		if (null == this.getTickets()) {
			this.manualTaskIdentityText = "";
		} else {
			this.setManualTaskIdentityText(this.getTickets().bubble().stream().map(Ticket::distinguishedName)
					.map(OrganizationDefinition::name).filter(Objects::nonNull).collect(Collectors.joining(",")));
		}
	}

	@PostLoad
	public void postLoad() {
		if (null != this.properties) {
			if (StringUtils.isNotEmpty(this.getProperties().getTitle())) {
				this.title = this.getProperties().getTitle();
			}
			this.splitValueList = this.getProperties().getSplitValueList();
			this.embedTargetJob = this.getProperties().getEmbedTargetJob();
			this.embedCompleted = this.getProperties().getEmbedCompleted();
			this.manualTaskIdentityMatrix = this.getProperties().getManualTaskIdentityMatrix();
			this.parentJob = this.getProperties().getParentJob();
			this.parentWork = this.getProperties().getParentWork();
			this.goBackStore = this.getProperties().getGoBackStore();
			this.goBackActivityToken = this.getProperties().getGoBackActivityToken();
			this.splitTokenValueMap = this.getProperties().getSplitTokenValueMap();
			this.tickets = this.getProperties().getTickets();
			this.serviceValue = this.getProperties().getServiceValue();
			this.manualEmpowerMap = this.getProperties().getManualEmpowerMap();
			this.forceRouteEnable = this.getProperties().getForceRouteEnable();
			this.manualTaskIdentityText = this.getProperties().getManualTaskIdentityText();
		}
	}

	/* 更新运行方法 */

	public Work() {
		this.properties = new WorkProperties();
	}

	public Work(Work work) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		this();
		Work copy = XGsonBuilder.convert(work, Work.class);
		copy.copyTo(this, JpaObject.id_FIELDNAME);
		this.copyProjectionFields(work);
	}

	public Work(WorkCompleted workCompleted) throws Exception {
		this();
		Work copy = XGsonBuilder.convert(workCompleted, Work.class);
		copy.copyTo(this, JpaObject.id_FIELDNAME);
		this.setId(workCompleted.getWork());
		this.copyProjectionFields(workCompleted);
	}

	public WorkProperties getProperties() {
		if (null == this.properties) {
			this.properties = new WorkProperties();
		}
		return this.properties;
	}

	public void setProperties(WorkProperties properties) {
		this.properties = properties;
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

	public String getWorkCreateType() {
		return StringUtils.equals(workCreateType, WORKCREATETYPE_ASSIGN) ? WORKCREATETYPE_ASSIGN
				: WORKCREATETYPE_SURFACE;
	}

	public List<String> getSplitValueList() {
		return this.splitValueList;
	}

	public void setSplitValueList(List<String> splitValueList) {
		this.splitValueList = splitValueList;
		this.getProperties().setSplitValueList(splitValueList);
	}

	public String getEmbedTargetJob() {
		return embedTargetJob;
	}

	public void setEmbedTargetJob(String embedTargetJob) {
		this.getProperties().setEmbedTargetJob(embedTargetJob);
		this.embedTargetJob = embedTargetJob;

	}

	public String getEmbedCompleted() {
		return embedCompleted;
	}

	public void setEmbedCompleted(String embedCompleted) {
		this.getProperties().setEmbedCompleted(embedCompleted);
		this.embedCompleted = embedCompleted;
	}

	public ManualTaskIdentityMatrix getManualTaskIdentityMatrix() {
		if (null == this.manualTaskIdentityMatrix) {
			this.manualTaskIdentityMatrix = this.getProperties().getManualTaskIdentityMatrix();
		}
		return this.manualTaskIdentityMatrix;
	}

	public void setManualTaskIdentityMatrix(ManualTaskIdentityMatrix manualTaskIdentityMatrix) {
		this.manualTaskIdentityMatrix = manualTaskIdentityMatrix;
		this.getProperties().setManualTaskIdentityMatrix(manualTaskIdentityMatrix);
	}

	public void setParentWork(String parentWork) {
		this.getProperties().setParentWork(parentWork);
		this.parentWork = parentWork;

	}

	public String getParentWork() {
		return parentWork;
	}

	public void setParentJob(String parentJob) {
		this.getProperties().setParentJob(parentJob);
		this.parentJob = parentJob;
	}

	public String getParentJob() {
		return parentJob;
	}

	public GoBackStore getGoBackStore() {
		return goBackStore;
	}

	public void setGoBackStore(GoBackStore goBackStore) {
		this.goBackStore = goBackStore;
		this.getProperties().setGoBackStore(goBackStore);
	}

	public String getGoBackActivityToken() {
		return goBackActivityToken;
	}

	public void setGoBackActivityToken(String goBackActivityToken) {
		this.goBackActivityToken = goBackActivityToken;
		this.getProperties().setGoBackActivityToken(goBackActivityToken);
	}

	public Map<String, String> getSplitTokenValueMap() {
		return splitTokenValueMap;
	}

	public void setSplitTokenValueMap(Map<String, String> splitTokenValueMap) {
		this.splitTokenValueMap = splitTokenValueMap;
		this.getProperties().setSplitTokenValueMap(splitTokenValueMap);
	}

	public void setTickets(Tickets tickets) {
		this.getProperties().setTickets(tickets);
		this.tickets = tickets;
	}

	public Tickets getTickets() {
		return tickets;
	}

	public Map<String, Object> getServiceValue() {
		return this.serviceValue;
	}

	public void setServiceValue(Map<String, Object> serviceValue) {
		this.getProperties().setServiceValue(serviceValue);
		this.serviceValue = serviceValue;
	}

	public Map<String, String> getManualEmpowerMap() {
		return manualEmpowerMap;
	}

	public void setManualEmpowerMap(Map<String, String> manualEmpowerMap) {
		this.getProperties().setManualEmpowerMap(manualEmpowerMap);
		this.manualEmpowerMap = manualEmpowerMap;
	}

	public Boolean getForceRouteEnable() {
		if (null == this.forceRouteEnable) {
			this.forceRouteEnable = this.getProperties().getForceRouteEnable();
		}
		return forceRouteEnable;
	}

	public void setForceRouteEnable(Boolean forceRouteEnable) {
		this.getProperties().setForceRouteEnable(forceRouteEnable);
		this.forceRouteEnable = forceRouteEnable;
	}

	public String getManualTaskIdentityText() {
		if ((null == this.manualTaskIdentityText) && (null != this.properties)) {
			this.manualTaskIdentityText = this.getProperties().getManualTaskIdentityText();
		}
		return manualTaskIdentityText;
	}

	public void setManualTaskIdentityText(String manualTaskIdentityText) {
		this.getProperties().setManualTaskIdentityText(manualTaskIdentityText);
		this.manualTaskIdentityText = manualTaskIdentityText;
	}

	public static final String MANUALEMPOWERMAP_FIELDNAME = "manualEmpowerMap";
	@Transient
	@Deprecated(since = "8.2", forRemoval = true)
	@FieldDescribe("授权对象")
	private Map<String, String> manualEmpowerMap = new LinkedHashMap<>();

	public static final String SERVICEVALUE_FIELDNAME = "serviceValue";
	@Transient
	@FieldDescribe("服务回调值")
	private Map<String, Object> serviceValue = new LinkedHashMap<>();

	public static final String GOBACKACTIVITYTOKEN_FIELDNAME = "goBackActivityToken";
	@Transient
	@FieldDescribe("goBack进行跳转退回时使用的.")
	private String goBackActivityToken;

	public static final String GOBACKSTORE_FIELDNAME = "goBackStore";
	@Transient
	@FieldDescribe("回退临时存储数据.")
	private GoBackStore goBackStore;

	public static final String SPLITVALUELIST_FIELDNAME = "splitValueList";
	@Transient
	@FieldDescribe("要拆分的值")
	private List<String> splitValueList;

	public static final String EMBEDTARGETJOB_FIELDNAME = "embedTargetJob";
	@Transient
	@FieldDescribe("Embed活动生成的Work的Job.")
	private String embedTargetJob;

	public static final String EMBEDCOMPLETED_FIELDNAME = "embedCompleted";
	@Transient
	@FieldDescribe("子流程返回标识.")
	private String embedCompleted;

	public static final String SPLITTOKENVALUEMAP_FIELDNAME = "splitTokenValueMap";
	@Transient
	@FieldDescribe("拆分值存储对象.")
	private Map<String, String> splitTokenValueMap;

	public static final String MANUALTASKIDENTITYMATRIX_FIELDNAME = "manualTaskIdentityMatrix";
	@FieldDescribe("待办身份矩阵.")
	@Transient
	private ManualTaskIdentityMatrix manualTaskIdentityMatrix;

	public static final String PARENTWORK_FIELDNAME = "parentWork";
	@Transient
	@FieldDescribe("父工作,在当前工作是通过子流程调用时产生.")
	private String parentWork;

	public static final String PARENTJOB_FIELDNAME = "parentJob";
	@Transient
	@FieldDescribe("父工作Job,在当前工作是通过子流程调用时产生.")
	private String parentJob;

	public static final String TICKETS_FIELDNAME = "tickets";
	@Transient
	@FieldDescribe("待办凭证.")
	private Tickets tickets;

	public static final String FORCEROUTEENABLE_FIELDNAME = "forceRouteEnable";
	@Transient
	@FieldDescribe("强制路由.")
	private Boolean forceRouteEnable;

	public static final String MANUALTASKIDENTITYTEXT_FIELDNAME = "manualTaskIdentityText";
	@Transient
	@FieldDescribe("当前处理人身份合并文本,用','分割,此字段仅用于显示当前工作的处理人,不索引.")
	private String manualTaskIdentityText;

	public static final String job_FIELDNAME = "job";
	@FieldDescribe("工作")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + job_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + job_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String job;

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("标题")
	@Column(length = length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title;

	public static final String startTime_FIELDNAME = "startTime";
	@FieldDescribe("工作开始时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + startTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + startTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date startTime;

	public static final String startTimeMonth_FIELDNAME = "startTimeMonth";
	@FieldDescribe("用于在Filter中分类使用.")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + startTimeMonth_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + startTimeMonth_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String startTimeMonth;

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

	public static final String creatorUnitLevelName_FIELDNAME = "creatorUnitLevelName";
	@FieldDescribe("创建人组织层级名.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@CheckPersist(allowEmpty = true)
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + creatorUnitLevelName_FIELDNAME)
	private String creatorUnitLevelName;

	public static final String application_FIELDNAME = "application";
	@FieldDescribe("应用ID")
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
	@CheckPersist(allowEmpty = true)
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

	public static final String activity_FIELDNAME = "activity";
	@FieldDescribe("当前活动ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + activity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activity;

	public static final String activityType_FIELDNAME = "activityType";
	@FieldDescribe("活动类型.")
	@Enumerated(EnumType.STRING)
	@Column(length = ActivityType.length, name = ColumnNamePrefix + activityType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private ActivityType activityType;

	public static final String activityName_FIELDNAME = "activityName";
	@FieldDescribe("活动名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + activityName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityName;

	public static final String activityAlias_FIELDNAME = "activityAlias";
	@FieldDescribe("活动别名.")
	@Column(length = length_255B, name = ColumnNamePrefix + activityAlias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityAlias;

	public static final String activityDescription_FIELDNAME = "activityDescription";
	@FieldDescribe("活动说明.")
	@Column(length = length_255B, name = ColumnNamePrefix + activityDescription_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityDescription_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityDescription;

	public static final String activityToken_FIELDNAME = "activityToken";
	@FieldDescribe("活动的标识号，每进入一次活动将重新生成一次")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + activityToken_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityToken_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String activityToken;

	public static final String activityArrivedTime_FIELDNAME = "activityArrivedTime";
	@FieldDescribe("活动到达时间")
	@Column(name = ColumnNamePrefix + activityArrivedTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityArrivedTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date activityArrivedTime;

	public static final String serial_FIELDNAME = "serial";
	@FieldDescribe("编号")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + serial_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + serial_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String serial;

	public static final String dataChanged_FIELDNAME = "dataChanged";
	@FieldDescribe("当前工作是否经过保存修改的操作,用于判断是否是默认生成的未经修改的.")
	@Column(name = ColumnNamePrefix + dataChanged_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean dataChanged;

	public static final String workThroughManual_FIELDNAME = "workThroughManual";
	@FieldDescribe("是否已经经过人工节点,用于判断是否是草稿.在到达环节进行判断.")
	@Column(name = ColumnNamePrefix + workThroughManual_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean workThroughManual;

	public static final String workCreateType_FIELDNAME = "workCreateType";
	@FieldDescribe("工作创建类型,surface,assgin")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + workCreateType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workCreateType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workCreateType;

	public static final String workStatus_FIELDNAME = "workStatus";
	@FieldDescribe("工作状态")
	@Enumerated(EnumType.STRING)
	@Column(length = WorkStatus.length, name = ColumnNamePrefix + workStatus_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workStatus_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private WorkStatus workStatus;

	public static final String beforeExecuted_FIELDNAME = "beforeExecuted";
	@FieldDescribe("是否已经通过执行前")
	@Column(name = ColumnNamePrefix + beforeExecuted_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean beforeExecuted;

//	@Deprecated(since = "8.2,使用tickets后将删除此字段.", forRemoval = true)
//	public static final String MANUALTASKIDENTITYTEXT_FIELDNAME = "manualTaskIdentityText";
//	@FieldDescribe("当前处理人身份合并文本,用','分割,超长截断,此字段仅用于显示当前工作的处理人,不索引.")
//	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + MANUALTASKIDENTITYTEXT_FIELDNAME)
//	@CheckPersist(allowEmpty = true)
//	private String manualTaskIdentityText;

	/** Split Attribute */
	public static final String splitting_FIELDNAME = "splitting";
	@FieldDescribe("是否是拆分中的工作")
	@Column(name = ColumnNamePrefix + splitting_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean splitting;

	public static final String splitToken_FIELDNAME = "splitToken";
	@FieldDescribe("拆分工作令牌")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + splitToken_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String splitToken;

	public static final String splitTokenList_FIELDNAME = "splitTokenList";
	@FieldDescribe("拆分工作产生的Token")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ splitTokenList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + splitTokenList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_id, name = ColumnNamePrefix + splitTokenList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + splitTokenList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> splitTokenList;

	public static final String splitValue_FIELDNAME = "splitValue";
	@FieldDescribe("拆分值")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + splitValue_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String splitValue;

	public static final String form_FIELDNAME = "form";
	@FieldDescribe("使用表单")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + form_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String form;

	public static final String destinationRoute_FIELDNAME = "destinationRoute";
	@FieldDescribe("到达使用的路由")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + destinationRoute_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String destinationRoute;

	public static final String destinationRouteName_FIELDNAME = "destinationRouteName";
	@FieldDescribe("到达使用的路由")
	@Column(length = length_255B, name = ColumnNamePrefix + destinationRouteName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String destinationRouteName;

	public static final String destinationActivityType_FIELDNAME = "destinationActivityType";
	@Enumerated(EnumType.STRING)
	@FieldDescribe("当前活动类型")
	@Column(length = ActivityType.length, name = ColumnNamePrefix + destinationActivityType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + destinationActivityType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private ActivityType destinationActivityType;

	public static final String destinationActivity_FIELDNAME = "destinationActivity";
	@FieldDescribe("目标活动的ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + destinationActivity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + destinationActivity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String destinationActivity;

	public static final String expireTime_FIELDNAME = "expireTime";
	@FieldDescribe("任务截止时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + expireTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + expireTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date expireTime;

	public static final String embedTargetWork_FIELDNAME = "embedTargetWork";
	@FieldDescribe("Embed活动生成的WorkId，用于在embed生成targetWork之后在inquire环节进行推动。")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + embedTargetWork_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + embedTargetWork_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String embedTargetWork;

	public static final String properties_FIELDNAME = "properties";
	@FieldDescribe("属性对象存储字段.")
	@Persistent(fetch = FetchType.EAGER)
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private WorkProperties properties;

	public static final String stringValue01_FIELDNAME = "stringValue01";
	@FieldDescribe("业务数据String值01.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue01;

	public static final String stringValue02_FIELDNAME = "stringValue02";
	@FieldDescribe("业务数据String值02.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue02;

	public static final String stringValue03_FIELDNAME = "stringValue03";
	@FieldDescribe("业务数据String值03.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue03_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue03_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue03;

	public static final String stringValue04_FIELDNAME = "stringValue04";
	@FieldDescribe("业务数据String值04.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue04_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue04_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue04;

	public static final String stringValue05_FIELDNAME = "stringValue05";
	@FieldDescribe("业务数据String值05.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue05_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue05_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue05;

	public static final String stringValue06_FIELDNAME = "stringValue06";
	@FieldDescribe("业务数据String值06.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue06_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue06_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue06;

	public static final String stringValue07_FIELDNAME = "stringValue07";
	@FieldDescribe("业务数据String值07.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue07_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue07_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue07;

	public static final String stringValue08_FIELDNAME = "stringValue08";
	@FieldDescribe("业务数据String值08.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue08_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue08_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue08;

	public static final String stringValue09_FIELDNAME = "stringValue09";
	@FieldDescribe("业务数据String值09.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue09_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue09_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue09;

	public static final String stringValue10_FIELDNAME = "stringValue10";
	@FieldDescribe("业务数据String值10.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue10_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue10_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue10;

	public static final String booleanValue01_FIELDNAME = "booleanValue01";
	@FieldDescribe("业务数据Boolean值01.")
	@Column(name = ColumnNamePrefix + booleanValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean booleanValue01;

	public static final String booleanValue02_FIELDNAME = "booleanValue02";
	@FieldDescribe("业务数据Boolean值02.")
	@Column(name = ColumnNamePrefix + booleanValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean booleanValue02;

	public static final String doubleValue01_FIELDNAME = "doubleValue01";
	@FieldDescribe("业务数据Double值01.")
	@Column(name = ColumnNamePrefix + doubleValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + doubleValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double doubleValue01;

	public static final String doubleValue02_FIELDNAME = "doubleValue02";
	@FieldDescribe("业务数据Double值02.")
	@Column(name = ColumnNamePrefix + doubleValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + doubleValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double doubleValue02;

	public static final String doubleValue03_FIELDNAME = "doubleValue03";
	@FieldDescribe("业务数据Double值03.")
	@Column(name = ColumnNamePrefix + doubleValue03_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + doubleValue03_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double doubleValue03;

	public static final String doubleValue04_FIELDNAME = "doubleValue04";
	@FieldDescribe("业务数据Double值04.")
	@Column(name = ColumnNamePrefix + doubleValue04_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + doubleValue04_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double doubleValue04;

	public static final String doubleValue05_FIELDNAME = "doubleValue05";
	@FieldDescribe("业务数据Double值05.")
	@Column(name = ColumnNamePrefix + doubleValue05_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + doubleValue05_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double doubleValue05;

	public static final String longValue01_FIELDNAME = "longValue01";
	@FieldDescribe("业务数据Long值01.")
	@Column(name = ColumnNamePrefix + longValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + longValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long longValue01;

	public static final String longValue02_FIELDNAME = "longValue02";
	@FieldDescribe("业务数据Long值02.")
	@Column(name = ColumnNamePrefix + longValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + longValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long longValue02;

	public static final String longValue03_FIELDNAME = "longValue03";
	@FieldDescribe("业务数据Long值03.")
	@Column(name = ColumnNamePrefix + longValue03_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + longValue03_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long longValue03;

	public static final String longValue04_FIELDNAME = "longValue04";
	@FieldDescribe("业务数据Long值04.")
	@Column(name = ColumnNamePrefix + longValue04_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + longValue04_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long longValue04;

	public static final String longValue05_FIELDNAME = "longValue05";
	@FieldDescribe("业务数据Long值05.")
	@Column(name = ColumnNamePrefix + longValue05_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + longValue05_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long longValue05;

	public static final String dateTimeValue01_FIELDNAME = "dateTimeValue01";
	@Temporal(TemporalType.TIMESTAMP)
	@FieldDescribe("业务数据DateTime值01.")
	@Column(name = ColumnNamePrefix + dateTimeValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateTimeValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateTimeValue01;

	public static final String dateTimeValue02_FIELDNAME = "dateTimeValue02";
	@Temporal(TemporalType.TIMESTAMP)
	@FieldDescribe("业务数据DateTime值02.")
	@Column(name = ColumnNamePrefix + dateTimeValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateTimeValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateTimeValue02;

	public static final String dateTimeValue03_FIELDNAME = "dateTimeValue03";
	@Temporal(TemporalType.TIMESTAMP)
	@FieldDescribe("业务数据DateTime值03.")
	@Column(name = ColumnNamePrefix + dateTimeValue03_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateTimeValue03_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateTimeValue03;

	public static final String dateTimeValue04_FIELDNAME = "dateTimeValue04";
	@Temporal(TemporalType.TIMESTAMP)
	@FieldDescribe("业务数据DateTime值04.")
	@Column(name = ColumnNamePrefix + dateTimeValue04_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateTimeValue04_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateTimeValue04;

	public static final String dateTimeValue05_FIELDNAME = "dateTimeValue05";
	@Temporal(TemporalType.TIMESTAMP)
	@FieldDescribe("业务数据DateTime值05.")
	@Column(name = ColumnNamePrefix + dateTimeValue05_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateTimeValue05_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateTimeValue05;

	public static final String dateValue01_FIELDNAME = "dateValue01";
	@Temporal(TemporalType.DATE)
	@FieldDescribe("业务数据Date值01.")
	@Column(name = ColumnNamePrefix + dateValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateValue01;

	public static final String dateValue02_FIELDNAME = "dateValue02";
	@Temporal(TemporalType.DATE)
	@FieldDescribe("业务数据Date值02.")
	@Column(name = ColumnNamePrefix + dateValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateValue02;

	public static final String timeValue01_FIELDNAME = "timeValue01";
	@Temporal(TemporalType.TIME)
	@FieldDescribe("业务数据Time值01.")
	@Column(name = ColumnNamePrefix + timeValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + timeValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date timeValue01;

	public static final String timeValue02_FIELDNAME = "timeValue02";
	@Temporal(TemporalType.TIME)
	@FieldDescribe("业务数据Time值02.")
	@Column(name = ColumnNamePrefix + timeValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + timeValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date timeValue02;

	public static final String OBJECTSECURITYCLEARANCE_FIELDNAME = "objectSecurityClearance";
	@FieldDescribe("客体密级标识.")
	@Column(name = ColumnNamePrefix + OBJECTSECURITYCLEARANCE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + OBJECTSECURITYCLEARANCE_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer objectSecurityClearance;

	public Integer getObjectSecurityClearance() {
		return objectSecurityClearance;
	}

	public void setObjectSecurityClearance(Integer objectSecurityClearance) {
		this.objectSecurityClearance = objectSecurityClearance;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public WorkStatus getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(WorkStatus workStatus) {
		this.workStatus = workStatus;
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

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getActivityToken() {
		return activityToken;
	}

	public void setActivityToken(String activityToken) {
		this.activityToken = activityToken;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Boolean getSplitting() {
		return splitting;
	}

	public void setSplitting(Boolean splitting) {
		this.splitting = splitting;
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

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getDestinationRoute() {
		return destinationRoute;
	}

	public void setDestinationRoute(String destinationRoute) {
		this.destinationRoute = destinationRoute;
	}

	public String getDestinationActivity() {
		return destinationActivity;
	}

	public void setDestinationActivity(String destinationActivity) {
		this.destinationActivity = destinationActivity;
	}

	public String getDestinationRouteName() {
		return destinationRouteName;
	}

	public void setDestinationRouteName(String destinationRouteName) {
		this.destinationRouteName = destinationRouteName;
	}

	public Date getActivityArrivedTime() {
		return activityArrivedTime;
	}

	public void setActivityArrivedTime(Date activityArrivedTime) {
		this.activityArrivedTime = activityArrivedTime;
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public ActivityType getDestinationActivityType() {
		return destinationActivityType;
	}

	public void setDestinationActivityType(ActivityType destinationActivityType) {
		this.destinationActivityType = destinationActivityType;
	}

	public String getStartTimeMonth() {
		return startTimeMonth;
	}

	public void setStartTimeMonth(String startTimeMonth) {
		this.startTimeMonth = startTimeMonth;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
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

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public String getEmbedTargetWork() {
		return embedTargetWork;
	}

	public void setEmbedTargetWork(String embedTargetWork) {
		this.embedTargetWork = embedTargetWork;
	}

	public Boolean getBeforeExecuted() {
		return beforeExecuted;
	}

	public void setBeforeExecuted(Boolean beforeExecuted) {
		this.beforeExecuted = beforeExecuted;
	}

	public String getCreatorUnitLevelName() {
		return creatorUnitLevelName;
	}

	public void setCreatorUnitLevelName(String creatorUnitLevelName) {
		this.creatorUnitLevelName = creatorUnitLevelName;
	}

	public String getActivityAlias() {
		return activityAlias;
	}

	public static String getActivitydescriptionFieldname() {
		return activityDescription_FIELDNAME;
	}

	public String getActivityDescription() {
		return activityDescription;
	}

	public void setActivityAlias(String activityAlias) {
		this.activityAlias = activityAlias;
	}

	public void setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
	}

	public Boolean getDataChanged() {
		return dataChanged;
	}

	public void setDataChanged(Boolean dataChanged) {
		this.dataChanged = dataChanged;
	}

	public String getStringValue01() {
		return stringValue01;
	}

	public void setStringValue01(String stringValue01) {
		this.stringValue01 = stringValue01;
	}

	public String getStringValue02() {
		return stringValue02;
	}

	public void setStringValue02(String stringValue02) {
		this.stringValue02 = stringValue02;
	}

	public String getStringValue03() {
		return stringValue03;
	}

	public void setStringValue03(String stringValue03) {
		this.stringValue03 = stringValue03;
	}

	public String getStringValue04() {
		return stringValue04;
	}

	public void setStringValue04(String stringValue04) {
		this.stringValue04 = stringValue04;
	}

	public String getStringValue05() {
		return stringValue05;
	}

	public void setStringValue05(String stringValue05) {
		this.stringValue05 = stringValue05;
	}

	public String getStringValue06() {
		return stringValue06;
	}

	public void setStringValue06(String stringValue06) {
		this.stringValue06 = stringValue06;
	}

	public String getStringValue07() {
		return stringValue07;
	}

	public void setStringValue07(String stringValue07) {
		this.stringValue07 = stringValue07;
	}

	public String getStringValue08() {
		return stringValue08;
	}

	public void setStringValue08(String stringValue08) {
		this.stringValue08 = stringValue08;
	}

	public String getStringValue09() {
		return stringValue09;
	}

	public void setStringValue09(String stringValue09) {
		this.stringValue09 = stringValue09;
	}

	public String getStringValue10() {
		return stringValue10;
	}

	public void setStringValue10(String stringValue10) {
		this.stringValue10 = stringValue10;
	}

	public Double getDoubleValue03() {
		return doubleValue03;
	}

	public void setDoubleValue03(Double doubleValue03) {
		this.doubleValue03 = doubleValue03;
	}

	public Double getDoubleValue04() {
		return doubleValue04;
	}

	public void setDoubleValue04(Double doubleValue04) {
		this.doubleValue04 = doubleValue04;
	}

	public Double getDoubleValue05() {
		return doubleValue05;
	}

	public void setDoubleValue05(Double doubleValue05) {
		this.doubleValue05 = doubleValue05;
	}

	public Long getLongValue03() {
		return longValue03;
	}

	public void setLongValue03(Long longValue03) {
		this.longValue03 = longValue03;
	}

	public Long getLongValue04() {
		return longValue04;
	}

	public void setLongValue04(Long longValue04) {
		this.longValue04 = longValue04;
	}

	public Long getLongValue05() {
		return longValue05;
	}

	public void setLongValue05(Long longValue05) {
		this.longValue05 = longValue05;
	}

	public Date getDateTimeValue03() {
		return dateTimeValue03;
	}

	public void setDateTimeValue03(Date dateTimeValue03) {
		this.dateTimeValue03 = dateTimeValue03;
	}

	public Date getDateTimeValue04() {
		return dateTimeValue04;
	}

	public void setDateTimeValue04(Date dateTimeValue04) {
		this.dateTimeValue04 = dateTimeValue04;
	}

	public Date getDateTimeValue05() {
		return dateTimeValue05;
	}

	public void setDateTimeValue05(Date dateTimeValue05) {
		this.dateTimeValue05 = dateTimeValue05;
	}

	public Boolean getBooleanValue01() {
		return booleanValue01;
	}

	public void setBooleanValue01(Boolean booleanValue01) {
		this.booleanValue01 = booleanValue01;
	}

	public Boolean getBooleanValue02() {
		return booleanValue02;
	}

	public void setBooleanValue02(Boolean booleanValue02) {
		this.booleanValue02 = booleanValue02;
	}

	public Double getDoubleValue01() {
		return doubleValue01;
	}

	public void setDoubleValue01(Double doubleValue01) {
		this.doubleValue01 = doubleValue01;
	}

	public Double getDoubleValue02() {
		return doubleValue02;
	}

	public void setDoubleValue02(Double doubleValue02) {
		this.doubleValue02 = doubleValue02;
	}

	public Long getLongValue01() {
		return longValue01;
	}

	public void setLongValue01(Long longValue01) {
		this.longValue01 = longValue01;
	}

	public Long getLongValue02() {
		return longValue02;
	}

	public void setLongValue02(Long longValue02) {
		this.longValue02 = longValue02;
	}

	public Date getDateTimeValue01() {
		return dateTimeValue01;
	}

	public void setDateTimeValue01(Date dateTimeValue01) {
		this.dateTimeValue01 = dateTimeValue01;
	}

	public Date getDateTimeValue02() {
		return dateTimeValue02;
	}

	public void setDateTimeValue02(Date dateTimeValue02) {
		this.dateTimeValue02 = dateTimeValue02;
	}

	public Date getDateValue01() {
		return dateValue01;
	}

	public void setDateValue01(Date dateValue01) {
		this.dateValue01 = dateValue01;
	}

	public Date getDateValue02() {
		return dateValue02;
	}

	public void setDateValue02(Date dateValue02) {
		this.dateValue02 = dateValue02;
	}

	public Date getTimeValue01() {
		return timeValue01;
	}

	public void setTimeValue01(Date timeValue01) {
		this.timeValue01 = timeValue01;
	}

	public Date getTimeValue02() {
		return timeValue02;
	}

	public void setTimeValue02(Date timeValue02) {
		this.timeValue02 = timeValue02;
	}

	public Boolean getWorkThroughManual() {
		return workThroughManual;
	}

	public void setWorkThroughManual(Boolean workThroughManual) {
		this.workThroughManual = workThroughManual;
	}

	public void setWorkCreateType(String workCreateType) {
		this.workCreateType = workCreateType;
	}

}