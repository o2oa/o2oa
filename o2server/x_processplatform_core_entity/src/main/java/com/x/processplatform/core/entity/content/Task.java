package com.x.processplatform.core.entity.content;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.PersistenceProperties;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Route;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Content.Task.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Content.Task.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Task extends SliceJpaObject {

	private static final long serialVersionUID = -5448210797584958826L;
	private static final String TABLE = PersistenceProperties.Content.Task.table;

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
		/* 将null值赋值为空保证Gson输出 */
		if (null == this.routeName) {
			this.routeName = "";
		}
		if (null == this.opinion) {
			this.setOpinion("");
		}
	}

	public String getOpinion() {
		if (StringUtils.isNotEmpty(this.opinionLob)) {
			return this.opinionLob;
		} else {
			return this.opinion;
		}
	}

	public void setOpinion(String opinion) {
		if (StringTools.utf8Length(opinion) > length_255B) {
			this.opinion = StringTools.utf8SubString(opinion, length_255B);
			this.opinionLob = opinion;
		} else {
			this.opinion = opinion;
			this.opinionLob = null;
		}
	}

	/* 更新运行方法 */

	public Task() {
	}

	public Task(Work work, String identity, String person, String unit, String trustIdentity, Date startTime,
			Date expireTime, List<Route> routes, Boolean allowRapid) {
		this.job = work.getJob();
		this.title = work.getTitle();
		this.startTime = startTime;
		this.work = work.getId();
		this.application = work.getApplication();
		this.applicationName = work.getApplicationName();
		this.applicationAlias = work.getApplicationAlias();
		this.process = work.getProcess();
		this.processName = work.getProcessName();
		this.processAlias = work.getProcessAlias();
		this.serial = work.getSerial();
		this.person = person;
		this.identity = identity;
		this.unit = unit;
		this.trustIdentity = trustIdentity;
		this.activity = work.getActivity();
		this.activityName = work.getActivityName();
		this.activityAlias = work.getActivityAlias();
		this.activityDescription = work.getActivityDescription();
		this.activityType = work.getActivityType();
		this.activityToken = work.getActivityToken();
		this.creatorPerson = work.getCreatorPerson();
		this.creatorIdentity = work.getCreatorIdentity();
		this.creatorUnit = work.getCreatorUnit();
		this.expireTime = expireTime;
		if (ListTools.isNotEmpty(routes)) {
			routes.stream().sorted(Comparator.comparing(Route::getOrderNumber, Comparator.nullsLast(Integer::compareTo))
					.thenComparing(Route::getUpdateTime, Date::compareTo)).forEach(o -> {
						this.routeList.add(o.getId());
						this.routeNameList.add(o.getName());
						this.routeOpinionList.add(StringUtils.trimToEmpty(o.getOpinion()));
						this.routeDecisionOpinionList.add(StringUtils.trimToEmpty(o.getDecisionOpinion()));
					});
		}
		this.routeName = "";
		this.opinion = "";
		this.modified = false;
		this.viewed = false;
		this.allowRapid = allowRapid;
	}

	public static final String job_FIELDNAME = "job";
	@FieldDescribe("任务.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + job_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + job_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String job;

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("标题.")
	@Column(length = length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title;

	public static final String startTime_FIELDNAME = "startTime";
	@FieldDescribe("开始时间.")
	@Temporal(TemporalType.TIMESTAMP)
	/* 开始时间不能为空,如果为空排序可能出错 */
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

	public static final String work_FIELDNAME = "work";
	@FieldDescribe("工作ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + work_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + work_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String work;

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
	@Column(length = length_255B, name = ColumnNamePrefix + processName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + processName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processName;

	public static final String processAlias_FIELDNAME = "processAlias";
	@FieldDescribe("流程别名.")
	@Column(length = length_255B, name = ColumnNamePrefix + processAlias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + processAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processAlias;

	public static final String serial_FIELDNAME = "serial";
	@FieldDescribe("编号")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + serial_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + serial_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String serial;

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("当前处理人")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String identity_FIELDNAME = "identity";
	@FieldDescribe("当前处理人Identity")
	@Column(length = length_255B, name = ColumnNamePrefix + identity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + identity_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String identity;

	public static final String unit_FIELDNAME = "unit";
	@FieldDescribe("当前处理人所在组织.")
	@Column(length = length_255B, name = ColumnNamePrefix + unit_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + unit_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String unit;

	public static final String trustIdentity_FIELDNAME = "trustIdentity";
	@FieldDescribe("委托人Identity")
	@Column(length = length_255B, name = ColumnNamePrefix + trustIdentity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + trustIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String trustIdentity;

	public static final String activity_FIELDNAME = "activity";
	@FieldDescribe("活动ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + activity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activity_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String activity;

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

	public static final String activityType_FIELDNAME = "activityType";
	@FieldDescribe("活动类型.")
	@Enumerated(EnumType.STRING)
	@Column(length = ActivityType.length, name = ColumnNamePrefix + activityType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private ActivityType activityType;

	public static final String activityToken_FIELDNAME = "activityToken";
	@FieldDescribe("活动Token.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + activityToken_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityToken_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String activityToken;

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("创建人")
	@Column(length = length_255B, name = ColumnNamePrefix + creatorPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorPerson_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;

	public static final String creatorIdentity_FIELDNAME = "creatorIdentity";
	@FieldDescribe("创建人Identity")
	@Column(length = length_255B, name = ColumnNamePrefix + creatorIdentity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity;

	public static final String creatorUnit_FIELDNAME = "creatorUnit";
	@FieldDescribe("创建人部门")
	@Column(length = length_255B, name = ColumnNamePrefix + creatorUnit_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorUnit_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorUnit;

	public static final String expireTime_FIELDNAME = "expireTime";
	@FieldDescribe("任务截止时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Index(name = TABLE + IndexNameMiddle + expireTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + expireTime_FIELDNAME)
	private Date expireTime;

	public static final String expired_FIELDNAME = "expired";
	@FieldDescribe("是否已经超时.")
	@Column(name = ColumnNamePrefix + expired_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + expired_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean expired;

	public static final String urgeTime_FIELDNAME = "urgeTime";
	@FieldDescribe("催办时间.")
	@Temporal(TemporalType.TIMESTAMP)
	/* 开始时间不能为空,如果为空排序可能出错 */
	@Column(name = ColumnNamePrefix + urgeTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + urgeTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date urgeTime;

	public static final String urged_FIELDNAME = "urged";
	@FieldDescribe("是否已经催办过.")
	@Column(name = ColumnNamePrefix + urged_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + urged_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean urged;

	public static final String routeList_FIELDNAME = "routeList";
	@FieldDescribe("当前活动可供选择的路由.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + routeList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + routeList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_id, name = ColumnNamePrefix + routeList_FIELDNAME)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@CheckPersist(allowEmpty = true)
	private List<String> routeList = new ArrayList<String>();

	public static final String routeNameList_FIELDNAME = "routeNameList";
	@FieldDescribe("当前活动可供选择的路由名称.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + routeNameList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + routeNameList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + routeNameList_FIELDNAME)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@CheckPersist(allowEmpty = true)
	private List<String> routeNameList = new ArrayList<String>();

	public static final String routeOpinionList_FIELDNAME = "routeOpinionList";
	@FieldDescribe("当前活动可供选择的路由对应的默认意见.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ routeOpinionList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + routeOpinionList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + routeOpinionList_FIELDNAME)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@CheckPersist(allowEmpty = true)
	private List<String> routeOpinionList = new ArrayList<String>();

	public static final String routeDecisionOpinionList_FIELDNAME = "routeDecisionOpinionList";
	@FieldDescribe("决策性意见列表,使用#分割.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ routeDecisionOpinionList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ routeDecisionOpinionList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + routeDecisionOpinionList_FIELDNAME)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@CheckPersist(allowEmpty = true)
	private List<String> routeDecisionOpinionList = new ArrayList<String>();

	public static final String routeName_FIELDNAME = "routeName";
	@FieldDescribe("选择的路由名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + routeName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String routeName;

	public static final String opinion_FIELDNAME = "opinion";
	@FieldDescribe("处理意见.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + opinion_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + opinion_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	protected String opinion;

	public static final String opinionLob_FIELDNAME = "opinionLob";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + opinionLob_FIELDNAME)
	private String opinionLob;

	public static final String modified_FIELDNAME = "modified";
	@FieldDescribe("是否在前台保存过数据.")
	@Column(name = ColumnNamePrefix + modified_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + modified_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean modified;

	public static final String viewed_FIELDNAME = "viewed";
	@FieldDescribe("是否查看过.")
	@Column(name = ColumnNamePrefix + viewed_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + viewed_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean viewed;

	public static final String allowRapid_FIELDNAME = "allowRapid";
	@FieldDescribe("允许快速处理.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + allowRapid_FIELDNAME)
	private Boolean allowRapid;

	public static final String mediaOpinion_FIELDNAME = "mediaOpinion";
	@FieldDescribe("多媒体意见.")
	@Column(length = length_255B, name = ColumnNamePrefix + mediaOpinion_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String mediaOpinion;

	public static final String first_FIELDNAME = "first";
	@FieldDescribe("是否是第一条待办.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + first_FIELDNAME)
	private Boolean first;

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
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

	public List<String> getRouteList() {
		return routeList;
	}

	public void setRouteList(List<String> routeList) {
		this.routeList = routeList;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getActivityToken() {
		return activityToken;
	}

	public void setActivityToken(String activityToken) {
		this.activityToken = activityToken;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public List<String> getRouteNameList() {
		return routeNameList;
	}

	public void setRouteNameList(List<String> routeNameList) {
		this.routeNameList = routeNameList;
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

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
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

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	// public ManualMode getManualMode() {
	// return manualMode;
	// }
	//
	// public void setManualMode(ManualMode manualMode) {
	// this.manualMode = manualMode;
	// }

	public String getStartTimeMonth() {
		return startTimeMonth;
	}

	public void setStartTimeMonth(String startTimeMonth) {
		this.startTimeMonth = startTimeMonth;
	}

	public Boolean getModified() {
		return modified;
	}

	public void setModified(Boolean modified) {
		this.modified = modified;
	}

	public Boolean getViewed() {
		return viewed;
	}

	public void setViewed(Boolean viewed) {
		this.viewed = viewed;
	}

	public Boolean getAllowRapid() {
		return allowRapid;
	}

	public void setAllowRapid(Boolean allowRapid) {
		this.allowRapid = allowRapid;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
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

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getOpinionLob() {
		return opinionLob;
	}

	public void setOpinionLob(String opinionLob) {
		this.opinionLob = opinionLob;
	}

	public Date getUrgeTime() {
		return urgeTime;
	}

	public void setUrgeTime(Date urgeTime) {
		this.urgeTime = urgeTime;
	}

	public Boolean getUrged() {
		return urged;
	}

	public void setUrged(Boolean urged) {
		this.urged = urged;
	}

	public String getActivityAlias() {
		return activityAlias;
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

	public Boolean getExpired() {
		return expired;
	}

	public void setExpired(Boolean expired) {
		this.expired = expired;
	}

	public List<String> getRouteOpinionList() {
		return routeOpinionList;
	}

	public void setRouteOpinionList(List<String> routeOpinionList) {
		this.routeOpinionList = routeOpinionList;
	}

	public String getMediaOpinion() {
		return mediaOpinion;
	}

	public void setMediaOpinion(String mediaOpinion) {
		this.mediaOpinion = mediaOpinion;
	}

	public List<String> getRouteDecisionOpinionList() {
		return routeDecisionOpinionList;
	}

	public void setRouteDecisionOpinionList(List<String> routeDecisionOpinionList) {
		this.routeDecisionOpinionList = routeDecisionOpinionList;
	}

	public String getTrustIdentity() {
		return trustIdentity;
	}

	public void setTrustIdentity(String trustIdentity) {
		this.trustIdentity = trustIdentity;
	}

	public Boolean getFirst() {
		return first;
	}

	public void setFirst(Boolean first) {
		this.first = first;
	}

}