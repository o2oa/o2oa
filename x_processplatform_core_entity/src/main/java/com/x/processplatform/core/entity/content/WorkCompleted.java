package com.x.processplatform.core.entity.content;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
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

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Content.WorkCompleted.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class WorkCompleted extends SliceJpaObject {

	private static final long serialVersionUID = 8340732901486828267L;
	private static final String TABLE = PersistenceProperties.Content.WorkCompleted.table;

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
		if (StringUtils.isEmpty(this.completedTimeMonth)) {
			this.completedTimeMonth = DateTools.format(this.completedTime, DateTools.format_yyyyMM);
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

	@EntityFieldDescribe("工作开始时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Index(name = TABLE + "_completedTime")
	@Column(name = "xcompletedTime")
	@CheckPersist(allowEmpty = false)
	private Date completedTime;

	@EntityFieldDescribe("用于在Filter中分类使用.由于是自动计算所以允许空")
	@Column(length = JpaObject.length_16B, name = "xcompletedTimeMonth")
	@Index(name = TABLE + "_completedTimeMonth")
	@CheckPersist(allowEmpty = true)
	private String completedTimeMonth;

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
	@CheckPersist(allowEmpty = false)
	private String process;

	@EntityFieldDescribe("流程名称")
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

	@EntityFieldDescribe("使用表单ID")
	@Column(length = JpaObject.length_id, name = "xform")
	@Index(name = TABLE + "_form")
	@CheckPersist(allowEmpty = true)
	private String form;

	@EntityFieldDescribe("文本内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = "xformData")
	@CheckPersist(allowEmpty = true)
	private String formData;

	@EntityFieldDescribe("移动端文本内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = "xformMobileData")
	@CheckPersist(allowEmpty = true)
	private String formMobileData;

	@EntityFieldDescribe("Work Id.")
	@Column(length = JpaObject.length_id, name = "xwork")
	@Index(name = TABLE + "_work")
	@CheckPersist(allowEmpty = true)
	private String work;

	@EntityFieldDescribe("附件列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_attachmentList", joinIndex = @Index(name = TABLE + "_attachmentList_join"))
	@ElementColumn(length = JpaObject.length_id, name = "xattachmentList")
	@ElementIndex(name = TABLE + "_attachmentList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> attachmentList;

	@EntityFieldDescribe("任务截止时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Index(name = TABLE + "_expireTime")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xexpireTime")
	private Date expireTime;

	@EntityFieldDescribe("是否超时.")
	@Column(name = "xexpired")
	@Index(name = TABLE + "_expired")
	@CheckPersist(allowEmpty = false)
	private Boolean expired;

	@EntityFieldDescribe("工作时长(分钟数).")
	@Column(name = "duration")
	@CheckPersist(allowEmpty = false)
	private Long duration;

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
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

	public Date getCompletedTime() {
		return completedTime;
	}

	public void setCompletedTime(Date completedTime) {
		this.completedTime = completedTime;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public List<String> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(List<String> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getStartTimeMonth() {
		return startTimeMonth;
	}

	public void setStartTimeMonth(String startTimeMonth) {
		this.startTimeMonth = startTimeMonth;
	}

	public String getCompletedTimeMonth() {
		return completedTimeMonth;
	}

	public void setCompletedTimeMonth(String completedTimeMonth) {
		this.completedTimeMonth = completedTimeMonth;
	}

	public String getFormData() {
		return formData;
	}

	public void setFormData(String formData) {
		this.formData = formData;
	}

	public String getFormMobileData() {
		return formMobileData;
	}

	public void setFormMobileData(String formMobileData) {
		this.formMobileData = formMobileData;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
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

	public Boolean getExpired() {
		return expired;
	}

	public void setExpired(Boolean expired) {
		this.expired = expired;
	}

}