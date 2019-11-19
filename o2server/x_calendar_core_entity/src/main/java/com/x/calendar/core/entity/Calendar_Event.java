package com.x.calendar.core.entity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.ExDate;

/**
 * 日程事件信息
 * 
 * @author O2LEE
 *
 */
@SuppressWarnings("rawtypes")
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Calendar_Event.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Calendar_Event.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Calendar_Event extends SliceJpaObject implements Cloneable, Comparable {

	private static final long serialVersionUID = 1325197931747463979L;
	private static final String TABLE = PersistenceProperties.Calendar_Event.table;
	public static final int STRING_VALUE_MAX_LENGTH = JpaObject.length_255B;

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

	/*
	 * =============================================================================
	 * ===== 以上为 JpaObject 默认字段
	 * =============================================================================
	 * =====
	 */

	/*
	 * =============================================================================
	 * ===== 构造方法区
	 * =============================================================================
	 * =====
	 */
	public Calendar_Event() {
	}

	public Calendar_Event(final String id, final String title, final Date startTime, final Date endTime) {
		this.id = id;
		this.title = title;
		this.startTime = startTime;
		this.endTime = endTime;
		this.isAllDayEvent = false;
	}

	public Calendar_Event(final String id, final String title, final Date startTime, final int duration) {
		this.id = id;
		this.title = title;
		this.startTime = startTime;
		this.endTime = new Date(startTime.getTime() + duration);
		this.isAllDayEvent = false;
	}

	public Calendar_Event(final String id, final String title, final Date startTime, final int duration,
			final String recurrenceRule) {
		this(id, title, startTime, duration);
		this.recurrenceRule = recurrenceRule;
	}

	public Calendar_Event(final String id, final String title, final Date startTime, final Date endTime,
			final String recurrenceRule) {
		this(id, title, startTime, endTime);
		this.recurrenceRule = recurrenceRule;
	}

	/*
	 * =============================================================================
	 * ===== 以下为具体不同的业务及数据表字段要求
	 * =============================================================================
	 * =====
	 */
	public static final String calendarId_FIELDNAME = "calendarId";
	@FieldDescribe("日历账号ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + calendarId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + calendarId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String calendarId = null;

	public static final String repeatMasterId_FIELDNAME = "repeatMasterId";
	@FieldDescribe("重复日程宿主ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + repeatMasterId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + repeatMasterId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String repeatMasterId = null;

	public static final String eventType_FIELDNAME = "eventType";
	@FieldDescribe("信息类别: CAL_EVENT | TASK_EVENT")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + eventType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + eventType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String eventType = "CAL_EVENT";

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("事件标题")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + title_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String title = null;

	public static final String color_FIELDNAME = "color";
	@FieldDescribe("显示颜色")
	@Column(length = JpaObject.length_8B, name = ColumnNamePrefix + color_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String color = "#1462be";

	public static final String comment_FIELDNAME = "comment";
	@FieldDescribe("备注信息")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + comment_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String comment = null;

	public static final String commentId_FIELDNAME = "commentId";
	@FieldDescribe("备注LOB信息")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + commentId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String commentId = null;

	public static final String startTime_FIELDNAME = "startTime";
	@FieldDescribe("事件开始时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + startTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + startTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date startTime = null;

	public static final String startTimeStr_FIELDNAME = "startTimeStr";
	@FieldDescribe("事件开始时间(yyyy-MM-dd HH:mm:ss)")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + startTimeStr_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + startTimeStr_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String startTimeStr = null;

	public static final String endTime_FIELDNAME = "endTime";
	@FieldDescribe("事件结束时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + endTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + endTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date endTime = null;

	public static final String endTimeStr_FIELDNAME = "endTimeStr";
	@FieldDescribe("事件结束时间(yyyy-MM-dd HH:mm:ss)")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + endTimeStr_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + endTimeStr_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String endTimeStr = null;

	public static final String locationName_FIELDNAME = "locationName";
	@FieldDescribe("地点说明")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + locationName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String locationName = null;

	public static final String longitude_FIELDNAME = "longitude";
	@FieldDescribe("经度")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + longitude_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String longitude = null;

	public static final String latitude_FIELDNAME = "latitude";
	@FieldDescribe("纬度")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + latitude_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String latitude = null;

	public static final String recurrenceRule_FIELDNAME = "recurrenceRule";
	@FieldDescribe("重复日期表达式RecurrenceRule")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + recurrenceRule_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String recurrenceRule = null;

	public static final String recurrenceExc_FIELDNAME = "recurrenceExc";
	@FieldDescribe("排除日期表达式RecurrenceExc")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + recurrenceExc_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String recurrenceExc = null;

	public static final String alarm_FIELDNAME = "alarm";
	@FieldDescribe("是否提醒")
	@Column(name = ColumnNamePrefix + alarm_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean alarm = false;

	public static final String alarmTime_FIELDNAME = "alarmTime";
	@FieldDescribe("当前事件提醒时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + alarmTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date alarmTime = null;

	public static final String alarmAlready_FIELDNAME = "alarmAlready";
	@FieldDescribe("是否已经提醒过了")
	@Column(name = ColumnNamePrefix + alarmAlready_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean alarmAlready = false;

	public static final String valarmTime_config_FIELDNAME = "valarmTime_config";
	@FieldDescribe("提醒提配置：天, 时, 分, 秒（负数为提前）")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + valarmTime_config_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String valarmTime_config = "0,0,0,0";

	public static final String valarm_mailto_FIELDNAME = "valarm_mailto";
	@FieldDescribe("提交Email地址，为空则不邮件提醒")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + valarm_mailto_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String valarm_mailto = null;

	public static final String valarm_Summary_FIELDNAME = "valarm_Summary";
	@FieldDescribe("提醒显示标题")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + valarm_Summary_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String valarm_Summary = null;

	public static final String valarm_description_FIELDNAME = "valarm_description";
	@FieldDescribe("提醒备注信息")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + valarm_description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String valarm_description = null;

	public static final String isAllDayEvent_FIELDNAME = "isAllDayEvent";
	@FieldDescribe("是否为全天事件")
	@Column(name = ColumnNamePrefix + isAllDayEvent_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + isAllDayEvent_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean isAllDayEvent = false;

	public static final String daysOfDuration_FIELDNAME = "daysOfDuration";
	@FieldDescribe("事件持续天数：0、1或者N天")
	@Column(name = ColumnNamePrefix + daysOfDuration_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + daysOfDuration_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer daysOfDuration = 0;

	public static final String isPublic_FIELDNAME = "isPublic";
	@FieldDescribe("是否公开的事件")
	@Column(name = ColumnNamePrefix + isPublic_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + isPublic_FIELDNAME)
	private Boolean isPublic = false;

	public static final String source_FIELDNAME = "source";
	@FieldDescribe("信息来源: PERSONAL| LEADER | UNIT | MEETING | BUSINESS_TRIP | HOLIDAY")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + source_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + source_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String source = "PERSONAL";

	public static final String bundle_FIELDNAME = "bundle";
	@FieldDescribe("绑定来源数据标识")
	@Column(length = STRING_VALUE_MAX_LENGTH, name = ColumnNamePrefix + bundle_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + bundle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String bundle;

	public static final String createPerson_FIELDNAME = "createPerson";
	@FieldDescribe("创建者")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + createPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + createPerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String createPerson = null;

	public static final String updatePerson_FIELDNAME = "updatePerson";
	@FieldDescribe("最后更新者")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + updatePerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + updatePerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String updatePerson = null;

	public static final String targetType_FIELDNAME = "targetType";
	@FieldDescribe("参与者类型：PERSON | UNIT | GROUP")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + targetType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + targetType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String targetType = "PERSON";

	public static final String participants_FIELDNAME = "participants";
	@FieldDescribe("参与者列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + participants_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + participants_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ participants_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + participants_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> participants = null;

	public static final String manageablePersonList_FIELDNAME = "manageablePersonList";
	@FieldDescribe("可管理人员列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ manageablePersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ manageablePersonList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ manageablePersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + manageablePersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> manageablePersonList;

	public static final String viewablePersonList_FIELDNAME = "viewablePersonList";
	@FieldDescribe("可见人员列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ viewablePersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ viewablePersonList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ viewablePersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + viewablePersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> viewablePersonList;

	public static final String viewableUnitList_FIELDNAME = "viewableUnitList";
	@FieldDescribe("可见组织列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ viewableUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + viewableUnitList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ viewableUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + viewableUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> viewableUnitList;

	public static final String viewableGroupList_FIELDNAME = "viewableGroupList";
	@FieldDescribe("可见群组列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ viewableGroupList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ viewableGroupList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ viewableGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + viewableGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> viewableGroupList;

	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public String getCalendarId() {
		return calendarId;
	}

	public String getEventType() {
		return eventType;
	}

	public String getTitle() {
		return title;
	}

	public String getComment() {
		return comment;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public String getLocationName() {
		return locationName;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getRecurrenceRule() {
		return recurrenceRule;
	}

	public String getRecurrenceExc() {
		return recurrenceExc;
	}

	public String getValarm_mailto() {
		return valarm_mailto;
	}

	public String getValarm_Summary() {
		return valarm_Summary;
	}

	public String getValarm_description() {
		return valarm_description;
	}

	public Boolean getIsAllDayEvent() {
		return isAllDayEvent;
	}

	public String getSource() {
		return source;
	}

	public String getCreatePerson() {
		return createPerson;
	}

	public String getUpdatePerson() {
		return updatePerson;
	}

	public String getTargetType() {
		return targetType;
	}

	public List<String> getParticipants() {
		if (this.participants == null) {
			this.participants = new ArrayList<>();
		}
		return participants;
	}

	public List<String> getManageablePersonList() {
		if (this.manageablePersonList == null) {
			this.manageablePersonList = new ArrayList<>();
		}
		return manageablePersonList;
	}

	public List<String> getViewablePersonList() {
		if (this.viewablePersonList == null) {
			this.viewablePersonList = new ArrayList<>();
		}
		return viewablePersonList;
	}

	public List<String> getViewableUnitList() {
		if (this.viewableUnitList == null) {
			this.viewableUnitList = new ArrayList<>();
		}
		return viewableUnitList;
	}

	public List<String> getViewableGroupList() {
		if (this.viewableGroupList == null) {
			this.viewableGroupList = new ArrayList<>();
		}
		return viewableGroupList;
	}

	public void setCalendarId(String calendarId) {
		this.calendarId = calendarId;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public void setRecurrenceRule(String recurrenceRule) {
		this.recurrenceRule = recurrenceRule;
	}

	public void setRecurrenceExc(String recurrenceExc) {
		this.recurrenceExc = recurrenceExc;
	}

	public void setValarm_mailto(String valarm_mailto) {
		this.valarm_mailto = valarm_mailto;
	}

	public void setValarm_Summary(String valarm_Summary) {
		this.valarm_Summary = valarm_Summary;
	}

	public void setValarm_description(String valarm_description) {
		this.valarm_description = valarm_description;
	}

	public void setIsAllDayEvent(Boolean isAllDayEvent) {
		this.isAllDayEvent = isAllDayEvent;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setCreatePerson(String createPerson) {
		this.createPerson = createPerson;
	}

	public void setUpdatePerson(String updatePerson) {
		this.updatePerson = updatePerson;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public void setParticipants(List<String> participants) {
		this.participants = participants;
	}

	public void setManageablePersonList(List<String> manageablePersonList) {
		this.manageablePersonList = manageablePersonList;
	}

	public void setViewablePersonList(List<String> viewablePersonList) {
		this.viewablePersonList = viewablePersonList;
	}

	public void setViewableUnitList(List<String> viewableUnitList) {
		this.viewableUnitList = viewableUnitList;
	}

	public void setViewableGroupList(List<String> viewableGroupList) {
		this.viewableGroupList = viewableGroupList;
	}

	public List<String> addParticipants(String participant) {
		getParticipants();
		if (!this.participants.contains(participant)) {
			this.participants.add(participant);
		}
		return participants;
	}

	public List<String> addManageablePerson(String manageablePerson) {
		getManageablePersonList();
		if (!this.manageablePersonList.contains(manageablePerson)) {
			this.manageablePersonList.add(manageablePerson);
		}
		return this.manageablePersonList;
	}

	public List<String> addViewablePerson(String viewablePerson) {
		getViewablePersonList();
		if (!this.viewablePersonList.contains(viewablePerson)) {
			this.viewablePersonList.add(viewablePerson);
		}
		return this.viewablePersonList;
	}

	public List<String> addViewableUnit(String viewableUnit) {
		getViewableUnitList();
		if (!this.viewableUnitList.contains(viewableUnit)) {
			this.viewableUnitList.add(viewableUnit);
		}
		return this.viewableUnitList;
	}

	public List<String> addViewableGroup(String viewableGroup) {
		getViewableGroupList();
		if (!this.viewableGroupList.contains(viewableGroup)) {
			this.viewableGroupList.add(viewableGroup);
		}
		return this.viewableGroupList;
	}

	public String getRepeatMasterId() {
		return repeatMasterId;
	}

	public void setRepeatMasterId(String repeatMasterId) {
		this.repeatMasterId = repeatMasterId;
	}

	public Boolean getAlarm() {
		return alarm;
	}

	public Date getAlarmTime() {
		return alarmTime;
	}

	public void setAlarm(Boolean alarm) {
		this.alarm = alarm;
	}

	public void setAlarmTime(Date alarmTime) {
		this.alarmTime = alarmTime;
	}

	public String getValarmTime_config() {
		return valarmTime_config;
	}

	public void setValarmTime_config(String valarmTime_config) {
		this.valarmTime_config = valarmTime_config;
	}

	public Boolean getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
	}

	public Integer getDaysOfDuration() {
		return daysOfDuration;
	}

	public void setDaysOfDuration(Integer daysOfDuration) {
		this.daysOfDuration = daysOfDuration;
	}

	public Boolean getAlarmAlready() {
		return alarmAlready;
	}

	public void setAlarmAlready(Boolean alarmAlready) {
		this.alarmAlready = alarmAlready;
	}

	public String getStartTimeStr() {
		return startTimeStr;
	}

	public String getEndTimeStr() {
		return endTimeStr;
	}

	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getCommentId() { return commentId; }

	public void setCommentId(String commentId) { this.commentId = commentId; }

	/**
	 * 是否是在一天内结束的日程或者事件
	 * 
	 * @return
	 */
	public boolean isWithinOneDay() {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(this.startTime);
		final int startDay = cal.get(Calendar.DAY_OF_YEAR);
		cal.setTime(this.endTime);
		final int endDay = cal.get(Calendar.DAY_OF_YEAR);
		return (endDay - startDay == 0);
	}

	/**
	 * 是否为今天的日程或者事件
	 * 
	 * @return
	 */
	public boolean isToday() {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(this.startTime);
		final int startDay = cal.get(Calendar.DAY_OF_YEAR);
		cal.setTime(this.endTime);
		final int endDay = cal.get(Calendar.DAY_OF_YEAR);
		final int todayDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
		return (todayDay - startDay == 0) && ((todayDay - endDay == 0));
	}

	/**
	 * 克隆一个新的日程或者事件对象 Calendar_Record
	 */
	@Override
	public Calendar_Event clone() {
		Object c = null;
		try {
			c = super.clone();
		} catch (final CloneNotSupportedException e) {
			return null;
		}
		return (Calendar_Event) c;
	}

	/**
	 * 判断事件是否在指定的时间范围之内 Check if the event recurs within the given period
	 * @param periodStart
	 * @param periodEnd
	 * @return
	 */
	public boolean isRecurringInPeriod(final Date periodStart, final Date periodEnd) {
		final DateList recurDates = getRecurringsInPeriod(periodStart, periodEnd);
		return (recurDates != null && !recurDates.isEmpty());
	}

	/**
	 * 获取日程事件在指定时间范围内的执行时间列表
	 * 
	 * @param periodStart
	 * @param periodEnd
	 * @return
	 */
	private DateList getRecurringsInPeriod(final Date periodStart, final Date periodEnd) {
		DateList recurDates = null;
		final String recurrenceRule = getRecurrenceRule();
		if (recurrenceRule != null && !recurrenceRule.equals("")) {
			try {
				final Recur recur = new Recur(recurrenceRule);
				final net.fortuna.ical4j.model.Date periodStartDate = new net.fortuna.ical4j.model.Date(periodStart);
				final net.fortuna.ical4j.model.Date periodEndDate = new net.fortuna.ical4j.model.Date(periodEnd);
				final net.fortuna.ical4j.model.Date eventStartDate = new net.fortuna.ical4j.model.Date(getStartTime());
				recurDates = recur.getDates(eventStartDate, periodStartDate, periodEndDate, Value.DATE);
			} catch (final ParseException e) {
				System.out.println("cannot restore recurrence rule: " + recurrenceRule);
				e.printStackTrace();
			}

			final String recurrenceExc = getRecurrenceExc();
			if (recurrenceExc != null && !recurrenceExc.equals("")) {
				try {
					final ExDate exdate = new ExDate();
					if (recurrenceExc.length() > 8) {
						exdate.setValue(recurrenceExc);
					} else {
						exdate.getParameters().replace(Value.DATE);
						exdate.setValue(recurrenceExc);
					}
					for (final Object date : exdate.getDates()) {
						if (recurDates.contains(date)) {
							recurDates.remove(date);
						}
					}
				} catch (final ParseException e) {
					System.out.println("cannot restore excluded dates for this recurrence " + recurrenceRule);
					e.printStackTrace();
				}
			}
		}
		return recurDates;
	}

	/**
	 * 日程事件对比，排序使用
	 */
	@Override
	public int compareTo(final Object o1) {
		if (!(o1 instanceof Calendar_Event)) {
			return -1;
		}
		final Calendar_Event event1 = (Calendar_Event) o1;
		return this.getStartTime().compareTo(event1.getStartTime());
	}
}