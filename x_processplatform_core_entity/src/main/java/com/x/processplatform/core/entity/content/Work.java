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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;
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
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;
import com.x.processplatform.core.entity.PersistenceProperties;
import com.x.processplatform.core.entity.element.ActivityType;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Content.Work.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Work extends SliceJpaObject {

	private static final long serialVersionUID = 7668822947307502058L;
	private static final String TABLE = PersistenceProperties.Content.Work.table;

	@PrePersist
	public void prePersist() {
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
	public void preUpdate() {
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

	private void onPersist() {
		if (StringUtils.isEmpty(this.startTimeMonth)) {
			this.startTimeMonth = DateTools.format(this.startTime, DateTools.format_yyyyMM);
		}
	}

	/* 更新运行方法 */

	@EntityFieldDescribe("工作")
	@Column(length = JpaObject.length_id, name = "xjob")
	@Index(name = TABLE + "_job")
	@CheckPersist(allowEmpty = false)
	private String job;

	@EntityFieldDescribe("标题")
	@Column(length = AbstractPersistenceProperties.processPlatform_title_length, name = "xtitle")
	@Index(name = TABLE + "_title")
	@CheckPersist(allowEmpty = true)
	private String title;

	@EntityFieldDescribe("工作开始时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Index(name = TABLE + "_startTime")
	@Column(name = "xstartTime")
	@CheckPersist(allowEmpty = false)
	private Date startTime;

	@EntityFieldDescribe("用于在Filter中分类使用.")
	@Column(length = JpaObject.length_16B, name = "xstartTimeMonth")
	@Index(name = TABLE + "_startTimeMonth")
	@CheckPersist(allowEmpty = true)
	private String startTimeMonth;

	@EntityFieldDescribe("创建人，可能为空，如果由系统创建。")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xcreatorPerson")
	@Index(name = TABLE + "_creatorPerson")
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;

	@EntityFieldDescribe("创建人Identity，可能为空，如果由系统创建。")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xcreatorIdentity")
	@Index(name = TABLE + "_creatorIdentity")
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity;

	@EntityFieldDescribe("创建人部门，可能为空，如果由系统创建。")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xcreatorDepartment")
	@Index(name = TABLE + "_creatorDepartment")
	@CheckPersist(allowEmpty = true)
	private String creatorDepartment;

	@EntityFieldDescribe("创建人公司，可能为空，如果由系统创建。")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xcreatorCompany")
	@Index(name = TABLE + "_creatorCompany")
	@CheckPersist(allowEmpty = true)
	private String creatorCompany;

	@EntityFieldDescribe("应用ID")
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

	@EntityFieldDescribe("流程ID")
	@Column(length = JpaObject.length_id, name = "xprocess")
	@Index(name = TABLE + "_process")
	@CheckPersist(allowEmpty = true)
	private String process;

	@EntityFieldDescribe("流程名称")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xprocessName")
	@Index(name = TABLE + "_processName")
	@CheckPersist(allowEmpty = true)
	private String processName;

	@EntityFieldDescribe("流程别名")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xprocessAlias")
	@Index(name = TABLE + "_processAlias")
	@CheckPersist(allowEmpty = true)
	private String processAlias;

	@EntityFieldDescribe("编号")
	@Column(length = JpaObject.length_128B, name = "xserial")
	@Index(name = TABLE + "_serial")
	@CheckPersist(allowEmpty = true)
	private String serial;

	@EntityFieldDescribe("当前活动ID")
	@Column(length = JpaObject.length_id, name = "xactivity")
	@Index(name = TABLE + "_activity")
	@CheckPersist(allowEmpty = true)
	private String activity;

	@EntityFieldDescribe("活动类型.")
	@Enumerated(EnumType.STRING)
	@Column(length = ActivityType.length, name = "xactivityType")
	@Index(name = TABLE + "_activityType")
	@CheckPersist(allowEmpty = true)
	private ActivityType activityType;

	@EntityFieldDescribe("活动名称")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xactivityName")
	@Index(name = TABLE + "_activityName")
	@CheckPersist(allowEmpty = true)
	private String activityName;

	@EntityFieldDescribe("活动的标识号，每进入一次活动将重新生成一次")
	@Column(length = JpaObject.length_id, name = "xactivityToken")
	@Index(name = TABLE + "_activityToken")
	@CheckPersist(allowEmpty = false)
	private String activityToken;

	@EntityFieldDescribe("活动到达时间")
	@Index(name = TABLE + "_activityArrivedTime")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xactivityArrivedTime")
	private Date activityArrivedTime;

	@EntityFieldDescribe("工作状态")
	@Enumerated(EnumType.STRING)
	@Column(length = WorkStatus.length, name = "xworkStatus")
	@Index(name = TABLE + "_workStatus")
	@CheckPersist(allowEmpty = false)
	private WorkStatus workStatus;

	@EntityFieldDescribe("是否已经完成executed")
	@Column(name = "xexecuted")
	@CheckPersist(allowEmpty = false)
	private Boolean executed;

	@EntityFieldDescribe("是否已经完成inquire")
	@Column(name = "xinquired")
	@CheckPersist(allowEmpty = false)
	private Boolean inquired;

	@EntityFieldDescribe("重试次数.")
	@Column(name = "xerrorRetry")
	@CheckPersist(allowEmpty = false)
	private Integer errorRetry;

	@EntityFieldDescribe("附件列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_attachmentList", joinIndex = @Index(name = TABLE + "_attachmentList_join"))
	@ElementColumn(length = JpaObject.length_id, name = "xattachmentList")
	@ElementIndex(name = TABLE + "_attachmentList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> attachmentList;

	@EntityFieldDescribe("人工活动第一次到达是否运行")
	@Column(name = "xarrivedExecuted")
	@CheckPersist(allowEmpty = true)
	private Boolean arrivedExecuted;

	/* Manual Attribute */
	@EntityFieldDescribe("预期的处理人")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE
			+ "_manualTaskIdentityListt", joinIndex = @Index(name = TABLE + "_manualTaskIdentityList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xmanualTaskIdentityList")
	@ElementIndex(name = TABLE + "_manualTaskIdentityList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> manualTaskIdentityList;

	/* Split Attribute */
	@EntityFieldDescribe("是否是拆分中的工作")
	@Index(name = TABLE + "_splitting")
	@Column(name = "xsplitting")
	@CheckPersist(allowEmpty = false)
	private Boolean splitting;

	@EntityFieldDescribe("拆分工作令牌")
	@Column(length = JpaObject.length_id, name = "xsplitToken")
	@CheckPersist(allowEmpty = true)
	private String splitToken;

	@EntityFieldDescribe("拆分工作产生的Token")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_splitTokenList", joinIndex = @Index(name = TABLE + "_splitTokenList_join"))
	@ElementColumn(length = JpaObject.length_id, name = "xsplitTokenList")
	@ElementIndex(name = TABLE + "_splitTokenList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> splitTokenList;

	// @EntityFieldDescribe("拆分工作的路由")
	// @Column(length = JpaObject.length_id)
	// @CheckPersist
	// private String splitRoute;

	@EntityFieldDescribe("拆分值")
	@Column(length = JpaObject.length_255B, name = "xsplitValue")
	@CheckPersist(allowEmpty = true)
	private String splitValue;

	@EntityFieldDescribe("Service活动环节回写的值ID")
	@Column(length = JpaObject.length_id, name = "xserviceValue")
	@Index(name = TABLE + "_serviceValue")
	@CheckPersist(allowEmpty = true)
	private String serviceValue;

	// @EntityFieldDescribe("拆分工作产生的Token")
	// @PersistentCollection(fetch = FetchType.EAGER)
	// @OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	// @ContainerTable(name = TABLE + "_splitValueList", joinIndex = @Index(name
	// = TABLE + "_splitValueList_join") )
	// @ElementColumn(length = JpaObject.length_id)
	// @ElementIndex(name = TABLE + "_splitValueList_element")
	// @CheckPersist
	// private List<String> splitValueList;

	@EntityFieldDescribe("使用表单")
	@Column(length = JpaObject.length_id, name = "xform")
	@CheckPersist(allowEmpty = true)
	private String form;

	@EntityFieldDescribe("到达使用的路由")
	@Column(length = JpaObject.length_id, name = "xdestinationRoute")
	@CheckPersist(allowEmpty = true)
	private String destinationRoute;

	@EntityFieldDescribe("到达使用的路由")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xdestinationRouteName")
	@CheckPersist(allowEmpty = true)
	private String destinationRouteName;

	@Enumerated(EnumType.STRING)
	@EntityFieldDescribe("当前活动类型")
	@Column(length = ActivityType.length, name = "xdestinationActivityType")
	@Index(name = TABLE + "_destinationActivityType")
	@CheckPersist(allowEmpty = true)
	private ActivityType destinationActivityType;

	@EntityFieldDescribe("目标活动的ID")
	@Column(length = JpaObject.length_id, name = "xdestinationActivity")
	@Index(name = TABLE + "_destinationActivity")
	@CheckPersist(allowEmpty = true)
	private String destinationActivity;

	@EntityFieldDescribe("强制路由，用于调度等需要跳过执行环节直接进行的。")
	@Column(name = "xforceRoute")
	@CheckPersist(allowEmpty = true)
	private Boolean forceRoute;

	@EntityFieldDescribe("流程流转中的提醒信息")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_hintList", joinIndex = @Index(name = TABLE + "_hintList_join"))
	@ElementColumn(length = JpaObject.length_255B, name = "xhintList")
	// @ElementIndex(name = TABLE + "_hintList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> hintList;

	@EntityFieldDescribe("任务截止时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Index(name = TABLE + "_expireTime")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xexpireTime")
	private Date expireTime;

	@EntityFieldDescribe("Embed活动生成的WorkId，用于在embed生成targetWork之后在inquire环节进行推动。")
	@Column(length = JpaObject.length_id, name = "xembedTargetWork")
	@Index(name = TABLE + "_embedTargetWork")
	@CheckPersist(allowEmpty = true)
	private String embedTargetWork;

	public String getTitle() {
		return title;
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

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public Boolean getExecuted() {
		return executed;
	}

	public void setExecuted(Boolean executed) {
		this.executed = executed;
	}

	public Integer getErrorRetry() {
		return errorRetry;
	}

	public void setErrorRetry(Integer errorRetry) {
		this.errorRetry = errorRetry;
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

	public Boolean getInquired() {
		return inquired;
	}

	public void setInquired(Boolean inquired) {
		this.inquired = inquired;
	}

	public String getDestinationRouteName() {
		return destinationRouteName;
	}

	public void setDestinationRouteName(String destinationRouteName) {
		this.destinationRouteName = destinationRouteName;
	}

	public List<String> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(List<String> attachmentList) {
		this.attachmentList = attachmentList;
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

	public List<String> getManualTaskIdentityList() {
		return manualTaskIdentityList;
	}

	public void setManualTaskIdentityList(List<String> manualTaskIdentityList) {
		this.manualTaskIdentityList = manualTaskIdentityList;
	}

	public ActivityType getDestinationActivityType() {
		return destinationActivityType;
	}

	public void setDestinationActivityType(ActivityType destinationActivityType) {
		this.destinationActivityType = destinationActivityType;
	}

	public Boolean getArrivedExecuted() {
		return arrivedExecuted;
	}

	public void setArrivedExecuted(Boolean arrivedExecuted) {
		this.arrivedExecuted = arrivedExecuted;
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

	public String getServiceValue() {
		return serviceValue;
	}

	public void setServiceValue(String serviceValue) {
		this.serviceValue = serviceValue;
	}

	public Boolean getForceRoute() {
		return forceRoute;
	}

	public void setForceRoute(Boolean forceRoute) {
		this.forceRoute = forceRoute;
	}

	public List<String> getHintList() {
		return hintList;
	}

	public void setHintList(List<String> hintList) {
		this.hintList = hintList;
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

}