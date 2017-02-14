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

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
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
@Table(name = PersistenceProperties.Content.WorkLog.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class WorkLog extends SliceJpaObject {

	private static final long serialVersionUID = 8673378766635237050L;

	private static final String TABLE = PersistenceProperties.Content.WorkLog.table;

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
	}

	/* 更新运行方法 */

	@EntityFieldDescribe("工作")
	@Column(length = JpaObject.length_id, name = "xjob")
	@Index(name = TABLE + "_job")
	@CheckPersist(allowEmpty = false)
	private String job;

	@EntityFieldDescribe("工作")
	@Column(length = JpaObject.length_id, name = "xwork")
	@Index(name = TABLE + "_work")
	@CheckPersist(allowEmpty = false)
	private String work;

	@EntityFieldDescribe("工作")
	@Column(length = JpaObject.length_id, name = "xworkCompleted")
	@Index(name = TABLE + "_workCompleted")
	@CheckPersist(allowEmpty = true)
	private String workCompleted;

	@EntityFieldDescribe("工作是否已经完成.")
	@Index(name = TABLE + "_completed")
	/* 必填值 */
	@Column(name = "xcompleted")
	@CheckPersist(allowEmpty = false)
	private Boolean completed;

	@EntityFieldDescribe("开始活动Id")
	@Column(length = JpaObject.length_id, name = "xfromActivity")
	@Index(name = TABLE + "_fromActivity")
	@CheckPersist(allowEmpty = false)
	private String fromActivity;

	@EntityFieldDescribe("开始活动类型.")
	@Index(name = TABLE + "_fromActivityType")
	@Enumerated(EnumType.STRING)
	@Column(length = ActivityType.length, name = "xfromActivityType")
	@CheckPersist(allowEmpty = false)
	private ActivityType fromActivityType;

	@EntityFieldDescribe("开始活动名称")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xfromActivityName")
	@Index(name = TABLE + "_fromActivityName")
	@CheckPersist(allowEmpty = true)
	private String fromActivityName;

	@EntityFieldDescribe("开始节点Token")
	@Column(length = JpaObject.length_id, name = "xfromActivityToken")
	@Index(name = TABLE + "_fromActivityToken")
	@CheckPersist(allowEmpty = false)
	private String fromActivityToken;

	@EntityFieldDescribe("开始时间.")
	@Index(name = TABLE + "_fromTime")
	@Column(name = "xfromTime")
	@CheckPersist(allowEmpty = false)
	private Date fromTime;

	@EntityFieldDescribe("结束活动Id，可能为空，如果是未Connected的流程记录")
	@Column(length = JpaObject.length_id, name = "xarrivedActivity")
	@Index(name = TABLE + "_arrivedActivity")
	@CheckPersist(allowEmpty = true)
	private String arrivedActivity;

	@EntityFieldDescribe("结束活动类型.")
	@Index(name = TABLE + "_arrivedActivityType")
	@Enumerated(EnumType.STRING)
	@Column(length = ActivityType.length, name = "xarrivedActivityType")
	@CheckPersist(allowEmpty = true)
	private ActivityType arrivedActivityType;

	@EntityFieldDescribe("结束活动名称。")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xarrivedActivityName")
	@Index(name = TABLE + "_arrivedActivityName")
	@CheckPersist(allowEmpty = true)
	private String arrivedActivityName;

	@EntityFieldDescribe("开始节点Token")
	@Column(length = JpaObject.length_id, name = "xarrivedActivityToken")
	@Index(name = TABLE + "_arrivedActivityToken")
	@CheckPersist(allowEmpty = true)
	private String arrivedActivityToken;

	@EntityFieldDescribe("完成时间.")
	@Index(name = TABLE + "_arrivedTime")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xarrivedTime")
	private Date arrivedTime;

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
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xporcessName")
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

	@EntityFieldDescribe("到达节点使用的route ID.")
	@Column(length = JpaObject.length_id, name = "xroute")
	@Index(name = TABLE + "_route")
	@CheckPersist(allowEmpty = true)
	private String route;

	@EntityFieldDescribe("到达节点使用Route Name.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xrouteName")
	@Index(name = TABLE + "_routeName")
	@CheckPersist(allowEmpty = true)
	private String routeName;

	@EntityFieldDescribe("是否已经完整填写了From和Arrived.")
	@Index(name = TABLE + "_connected")
	@Column(name = "xconnected")
	@CheckPersist(allowEmpty = false)
	private Boolean connected;

	@EntityFieldDescribe("工作时长(分钟数).")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xduration")
	private Long duration;

	/* 不需要索引 */
	@EntityFieldDescribe("是否是拆分中的工作,用于回溯时候将值改回去。")
	@Column(name = "splitting")
	@CheckPersist(allowEmpty = false)
	private Boolean splitting;

	/* 不需要索引 */
	@EntityFieldDescribe("拆分工作令牌,用于回溯时候将值改回去。")
	@Column(length = JpaObject.length_id, name = "xsplitToken")
	@CheckPersist(allowEmpty = true)
	private String splitToken;

	/* 不需要索引 */
	@EntityFieldDescribe("拆分值,用于回溯时候将值改回去。")
	@Column(length = JpaObject.length_255B, name = "xsplitValue")
	@CheckPersist(allowEmpty = true)
	private String splitValue;

	@EntityFieldDescribe("拆分工作产生的Token")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_splitTokenList", joinIndex = @Index(name = TABLE + "_splitTokenList_join"))
	@ElementColumn(length = JpaObject.length_id, name = "xsplitTokenList")
	/* 不需要索引 */
	// @ElementIndex(name = TABLE + "_splitTokenList_element")
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

	// public ProcessingType getProcessingType() {
	// return processingType;
	// }
	//
	// public void setProcessingType(ProcessingType processingType) {
	// this.processingType = processingType;
	// }

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