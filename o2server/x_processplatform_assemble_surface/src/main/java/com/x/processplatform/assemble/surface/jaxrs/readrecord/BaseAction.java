package com.x.processplatform.assemble.surface.jaxrs.readrecord;

import java.util.Date;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.core.entity.element.ActivityType;

abstract class BaseAction extends StandardJaxrsAction {

	public static class ReadRecord extends GsonPropertyObject {

		private static final long serialVersionUID = -7967027122452291358L;

		public static final String TYPE_READ = "read";
		public static final String TYPE_READ_COMPLETED = "readCompleted";

		@FieldDescribe("待阅或者已阅标识.")
		private String id;

		@FieldDescribe("类型:read|readCompleted.")
		private String type;

		@FieldDescribe("任务标识.")
		private String job;

		@FieldDescribe("工作标识.")
		private String work;

		@FieldDescribe("已完成工作标识.")
		private String workCompleted;

		@FieldDescribe("工作是否已完成.")
		private Boolean completed;

		@FieldDescribe("待阅标识.")
		private String read;

		@FieldDescribe("标题.")
		private String title;

		@FieldDescribe("应用标识.")
		private String application;

		@FieldDescribe("应用名称.")
		private String applicationName;

		@FieldDescribe("应用别名.")
		private String applicationAlias;

		@FieldDescribe("流程标识.")
		private String process;

		@FieldDescribe("流程名称.")
		private String processName;

		@FieldDescribe("流程别名.")
		private String processAlias;

		@FieldDescribe("编号.")
		private String serial;

		@FieldDescribe("当前处理人.")
		private String person;

		@FieldDescribe("当前处理人身份.")
		private String identity;

		@FieldDescribe("当前处理人所在组织.")
		private String unit;

		@FieldDescribe("处理意见.")
		protected String opinion;

		@FieldDescribe("活动标识.")
		private String activity;

		@FieldDescribe("活动名称.")
		private String activityName;

		@FieldDescribe("活动别名.")
		private String activityAlias;

		@FieldDescribe("活动说明.")
		private String activityDescription;

		@FieldDescribe("活动类型.")
		private ActivityType activityType;

		@FieldDescribe("活动Token.")
		private String activityToken;

		@FieldDescribe("创建人")
		private String creatorPerson;

		@FieldDescribe("创建人Identity")
		private String creatorIdentity;

		@FieldDescribe("创建人组织")
		private String creatorUnit;

		@FieldDescribe("开始时间.")
		private Date startTime;

		@FieldDescribe("用于在Filter中分类使用.")
		private String startTimeMonth;

		@FieldDescribe("read转成readCompleted的完成时间,也就是任务的完成时间.")
		private Date completedTime;

		@FieldDescribe("read转成readCompleted的完成月份,也就是任务的完成时间,用于filter过滤..")
		private String completedTimeMonth;

		@FieldDescribe("工作时长(分钟数).")
		private Long duration;

		@FieldDescribe("当前活动名称.")
		private String currentActivityName;

		@FieldDescribe("业务数据String值01.")
		private String stringValue01;

		@FieldDescribe("业务数据String值02.")
		private String stringValue02;

		@FieldDescribe("业务数据String值03.")
		private String stringValue03;

		@FieldDescribe("业务数据String值04.")
		private String stringValue04;

		@FieldDescribe("业务数据String值05.")
		private String stringValue05;

		@FieldDescribe("业务数据String值06.")
		private String stringValue06;

		@FieldDescribe("业务数据Boolean值01.")
		private Boolean booleanValue01;

		@FieldDescribe("业务数据Boolean值02.")
		private Boolean booleanValue02;

		@FieldDescribe("业务数据Double值01.")
		private Double doubleValue01;

		@FieldDescribe("业务数据Double值02.")
		private Double doubleValue02;

		@FieldDescribe("业务数据Long值01.")
		private Long longValue01;

		@FieldDescribe("业务数据Long值02.")
		private Long longValue02;

		@FieldDescribe("业务数据DateTime值01.")
		private Date dateTimeValue01;

		@FieldDescribe("业务数据DateTime值02.")
		private Date dateTimeValue02;

		@FieldDescribe("业务数据Date值01.")
		private Date dateValue01;

		@FieldDescribe("业务数据Date值02.")
		private Date dateValue02;

		@FieldDescribe("业务数据Time值01.")
		private Date timeValue01;

		@FieldDescribe("业务数据Time值02.")
		private Date timeValue02;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
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

		public String getRead() {
			return read;
		}

		public void setRead(String read) {
			this.read = read;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
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

		public String getApplicationAlias() {
			return applicationAlias;
		}

		public void setApplicationAlias(String applicationAlias) {
			this.applicationAlias = applicationAlias;
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

		public String getProcessAlias() {
			return processAlias;
		}

		public void setProcessAlias(String processAlias) {
			this.processAlias = processAlias;
		}

		public String getSerial() {
			return serial;
		}

		public void setSerial(String serial) {
			this.serial = serial;
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

		public String getOpinion() {
			return opinion;
		}

		public void setOpinion(String opinion) {
			this.opinion = opinion;
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

		public Date getStartTime() {
			return startTime;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public String getStartTimeMonth() {
			return startTimeMonth;
		}

		public void setStartTimeMonth(String startTimeMonth) {
			this.startTimeMonth = startTimeMonth;
		}

		public Date getCompletedTime() {
			return completedTime;
		}

		public void setCompletedTime(Date completedTime) {
			this.completedTime = completedTime;
		}

		public String getCompletedTimeMonth() {
			return completedTimeMonth;
		}

		public void setCompletedTimeMonth(String completedTimeMonth) {
			this.completedTimeMonth = completedTimeMonth;
		}

		public Long getDuration() {
			return duration;
		}

		public void setDuration(Long duration) {
			this.duration = duration;
		}

		public String getCurrentActivityName() {
			return currentActivityName;
		}

		public void setCurrentActivityName(String currentActivityName) {
			this.currentActivityName = currentActivityName;
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
	}

}
