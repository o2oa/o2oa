package com.x.processplatform.core.entity.content;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;
import com.x.processplatform.core.entity.PersistenceProperties;
import com.x.processplatform.core.entity.element.ActivityType;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Content.Review.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Review extends SliceJpaObject {

	private static final long serialVersionUID = -570048661936488247L;

	private static final String TABLE = PersistenceProperties.Content.Review.table;

	@PrePersist
	public void prePersist() throws Exception { 
		Date date = new Date();
		if (null == this.createTime) {
			this.createTime = date;
		}
		this.updateTime = date;
		if (null == this.sequence) {
			this.sequence = StringUtils.join(DateTools.compact(this.getCreateTime()), this.getId());
		}
		this.onPersist();
	}

	@PreUpdate
	public void preUpdate() throws Exception{
		this.updateTime = new Date();
		this.onPersist();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	@EntityFieldDescribe("创建时间,自动生成.")
	@Index(name = TABLE + "_createTime")
	@Column(name = "xcreateTime")
	private Date createTime;

	@EntityFieldDescribe("修改时间,自动生成.")
	@Index(name = TABLE + "_updateTime")
	@Column(name = "xupdateTime")
	private Date updateTime;

	@EntityFieldDescribe("列表序号,由创建时间以及ID组成.在保存时自动生成.")
	@Column(length = AbstractPersistenceProperties.length_sequence, name = "xsequence")
	@Index(name = TABLE + "_sequence")
	private String sequence;

	@EntityFieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = JpaObject.length_id, name = JpaObject.IDCOLUMN)
	@Index(name = TABLE + "_id")
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	private void onPersist() throws Exception{
		if (StringUtils.isEmpty(this.startTimeMonth)) {
			this.startTimeMonth = DateTools.format(this.startTime, DateTools.format_yyyyMM);
		}
	}

	/* 更新运行方法 */

	@EntityFieldDescribe("任务.")
	@Column(length = JpaObject.length_id, name = "xjob")
	@Index(name = TABLE + "_job")
	@CheckPersist(allowEmpty = false)
	private String job;

	@EntityFieldDescribe("工作ID.")
	@Column(length = JpaObject.length_id, name = "xwork")
	@Index(name = TABLE + "_work")
	@CheckPersist(allowEmpty = true)
	private String work;

	@EntityFieldDescribe("工作ID.")
	@Column(length = JpaObject.length_id, name = "xworkCompleted")
	@Index(name = TABLE + "_work")
	@CheckPersist(allowEmpty = true)
	private String workCompleted;

	@EntityFieldDescribe("工作是否已经完成.")
	@Index(name = TABLE + "_completed")
	@Column(name = "xcompleted")
	@CheckPersist(allowEmpty = false)
	private Boolean completed;

	@EntityFieldDescribe("标题.")
	@Column(length = AbstractPersistenceProperties.processPlatform_title_length, name = "xtitle")
	@Index(name = TABLE + "_title")
	@CheckPersist(allowEmpty = true)
	private String title;

	@EntityFieldDescribe("开始时间.")
	@Temporal(TemporalType.TIMESTAMP)
	/* 开始时间不能为空,如果为空排序可能出错 */
	@Column(name = "xstartTime")
	@Index(name = TABLE + "_startTime")
	@CheckPersist(allowEmpty = false)
	private Date startTime;

	@EntityFieldDescribe("用于在Filter中分类使用.")
	@Column(length = JpaObject.length_16B, name = "xstartTimeMonth")
	@Index(name = TABLE + "_startTimeMonth")
	@CheckPersist(allowEmpty = true)
	private String startTimeMonth;

	@EntityFieldDescribe("应用.")
	@Column(length = JpaObject.length_id, name = "xapplication")
	@Index(name = TABLE + "_application")
	@CheckPersist(allowEmpty = false)
	private String application;

	@EntityFieldDescribe("应用名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xapplicationName")
	@Index(name = TABLE + "_applicationName")
	@CheckPersist(allowEmpty = true)
	private String applicationName;

	@EntityFieldDescribe("应用别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xapplicationAlias")
	@Index(name = TABLE + "_applicationAlias")
	@CheckPersist(allowEmpty = true)
	private String applicationAlias;

	@EntityFieldDescribe("流程ID.")
	@Column(length = JpaObject.length_id, name = "xprocess")
	@Index(name = TABLE + "_process")
	@CheckPersist(allowEmpty = false)
	private String process;

	@EntityFieldDescribe("流程名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xprocessName")
	@Index(name = TABLE + "_processName")
	@CheckPersist(allowEmpty = true)
	private String processName;

	@EntityFieldDescribe("流程别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xprocessAlias")
	@Index(name = TABLE + "_processAlias")
	@CheckPersist(allowEmpty = true)
	private String processAlias;

	@EntityFieldDescribe("编号")
	@Column(length = JpaObject.length_128B, name = "xserial")
	@Index(name = TABLE + "_serial")
	@CheckPersist(allowEmpty = true)
	private String serial;

	@EntityFieldDescribe("人员，可以为空 ，记录启动活动的时候可能由程序启动")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xperson")
	@Index(name = TABLE + "_person")
	@CheckPersist(allowEmpty = true)
	private String person;

	@EntityFieldDescribe("Identity，可以为空 ，记录启动活动的时候可能由程序启动")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xidentity")
	@Index(name = TABLE + "_identity")
	@CheckPersist(allowEmpty = false)
	private String identity;

	@EntityFieldDescribe("部门，可以为空 ，记录启动活动的时候可能由程序启动")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xdepartment")
	@Index(name = TABLE + "_department")
	@CheckPersist(allowEmpty = false)
	private String department;

	@EntityFieldDescribe("公司,可以为空 ，记录启动活动的时候可能由程序启动")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xcompany")
	@Index(name = TABLE + "_company")
	@CheckPersist(allowEmpty = false)
	private String company;

	@EntityFieldDescribe("创建人")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xcreatorPerson")
	@Index(name = TABLE + "_creatorPerson")
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;

	@EntityFieldDescribe("创建人Identity")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xcreatorIdentity")
	@Index(name = TABLE + "_creatorIdentity")
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity;

	@EntityFieldDescribe("创建人部门")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xcreatorDepartment")
	@Index(name = TABLE + "_creatorDepartment")
	@CheckPersist(allowEmpty = true)
	private String creatorDepartment;

	@EntityFieldDescribe("创建人公司")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xcreatorCompany")
	@Index(name = TABLE + "_creatorCompany")
	@CheckPersist(allowEmpty = true)
	private String creatorCompany;

	@EntityFieldDescribe("Review类型")
	@Enumerated(EnumType.STRING)
	@Column(length = ReviewType.length, name = "xreviewType")
	@Index(name = TABLE + "_reviewType")
	@CheckPersist(allowEmpty = false)
	private ReviewType reviewType;

	@EntityFieldDescribe("活动ID.")
	@Column(length = JpaObject.length_id, name = "xactivity")
	@Index(name = TABLE + "_activity")
	@CheckPersist(allowEmpty = false)
	private String activity;

	@EntityFieldDescribe("活动名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xactivityName")
	@Index(name = TABLE + "_activityName")
	@CheckPersist(allowEmpty = true)
	private String activityName;

	@EntityFieldDescribe("活动类型.")
	@Enumerated(EnumType.STRING)
	@Column(length = ActivityType.length, name = "xactivityType")
	@Index(name = TABLE + "_activityType")
	@CheckPersist(allowEmpty = false)
	private ActivityType activityType;

	@EntityFieldDescribe("活动Token.")
	@Column(length = JpaObject.length_id, name = "xactivityToken")
	@Index(name = TABLE + "_activityToken")
	@CheckPersist(allowEmpty = false)
	private String activityToken;

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

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
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

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
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

	public ReviewType getReviewType() {
		return reviewType;
	}

	public void setReviewType(ReviewType reviewType) {
		this.reviewType = reviewType;
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

	public String getActivityToken() {
		return activityToken;
	}

	public void setActivityToken(String activityToken) {
		this.activityToken = activityToken;
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public String getStartTimeMonth() {
		return startTimeMonth;
	}

	public void setStartTimeMonth(String startTimeMonth) {
		this.startTimeMonth = startTimeMonth;
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

	public String getCreatorDepartment() {
		return creatorDepartment;
	}

	public void setCreatorDepartment(String creatorDepartment) {
		this.creatorDepartment = creatorDepartment;
	}

	public String getCreatorCompany() {
		return creatorCompany;
	}

	public void setCreatorCompany(String creatorCompany) {
		this.creatorCompany = creatorCompany;
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

}