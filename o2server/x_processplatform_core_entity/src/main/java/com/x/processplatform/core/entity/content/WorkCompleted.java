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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Content.WorkCompleted.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Content.WorkCompleted.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN })

		// @UniqueConstraint(name = PersistenceProperties.Content.WorkCompleted.table +
		// JpaObject.IndexNameMiddle
		// + "application", columnNames = { JpaObject.IDCOLUMN, "xapplication",
		// "xapplicationName",
		// "xapplicationAlias", "xprocess", "xprocessName", "xprocessAlias" })

})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class WorkCompleted extends SliceJpaObject {

	private static final long serialVersionUID = 8340732901486828267L;
	private static final String TABLE = PersistenceProperties.Content.WorkCompleted.table;

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
		if (StringUtils.isEmpty(this.completedTimeMonth) && (null != this.completedTime)) {
			this.completedTimeMonth = DateTools.format(this.completedTime, DateTools.format_yyyyMM);
		}
		if (StringUtils.isEmpty(this.creatorIdentity)) {
			this.creatorIdentity = "";
		}
		if (StringUtils.isEmpty(this.creatorUnit)) {
			this.creatorUnit = "";
		}
		if (StringUtils.isEmpty(this.creatorPerson)) {
			this.creatorPerson = "";
		}
		if (StringUtils.isEmpty(this.creatorUnitLevelName)) {
			this.creatorUnitLevelName = "";
		}
	}

	/* 更新运行方法 */

	public WorkCompleted() {

	}

	/**
	 * 通过Work创建WorkCompleted
	 */
	public WorkCompleted(Work work, Date completedTime, Long duration, String formData, String formMobileData) {
		this.job = work.getJob();
		this.title = work.getTitle();
		this.startTime = work.getStartTime();
		this.startTimeMonth = work.getStartTimeMonth();
		this.completedTime = completedTime;
		this.creatorPerson = work.getCreatorPerson();
		this.creatorIdentity = work.getCreatorIdentity();
		this.creatorUnit = work.getCreatorUnit();
		this.creatorUnitLevelName = work.getCreatorUnitLevelName();
		this.application = work.getApplication();
		this.applicationName = work.getApplicationName();
		this.applicationAlias = work.getApplicationAlias();
		this.process = work.getProcess();
		this.processName = work.getProcessName();
		this.processAlias = work.getProcessAlias();
		this.serial = work.getSerial();
		this.form = work.getForm();
		this.formData = formData;
		this.formMobileData = formMobileData;
		this.work = work.getId();
		this.expireTime = work.getExpireTime();
		if ((null != expireTime) && (completedTime.after(expireTime))) {
			this.expired = true;
		} else {
			this.expired = false;
		}
		this.duration = duration;
		/** 扩充字段 */
		// this.extensionBoolean01 = work.getExtensionBoolean01();
		// this.extensionBoolean02 = work.getExtensionBoolean02();
		// this.extensionBoolean03 = work.getExtensionBoolean03();
		// this.extensionBoolean04 = work.getExtensionBoolean04();
		// this.extensionBoolean05 = work.getExtensionBoolean05();
		// this.extensionDouble01 = work.getExtensionDouble01();
		// this.extensionDouble02 = work.getExtensionDouble02();
		// this.extensionDouble03 = work.getExtensionDouble03();
		// this.extensionDouble04 = work.getExtensionDouble04();
		// this.extensionDouble05 = work.getExtensionDouble05();
		// this.extensionString01 = work.getExtensionString01();
		// this.extensionString02 = work.getExtensionString02();
		// this.extensionString03 = work.getExtensionString03();
		// this.extensionString04 = work.getExtensionString04();
		// this.extensionString05 = work.getExtensionString05();
		// this.extensionString06 = work.getExtensionString06();
		// this.extensionString07 = work.getExtensionString07();
		// this.extensionString08 = work.getExtensionString08();
		// this.extensionString09 = work.getExtensionString09();
		// this.extensionString10 = work.getExtensionString10();
	}

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

	public static final String completedTime_FIELDNAME = "completedTime";
	@FieldDescribe("工作开始时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + completedTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + completedTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date completedTime;

	public static final String completedTimeMonth_FIELDNAME = "completedTimeMonth";
	@FieldDescribe("用于在Filter中分类使用.由于是自动计算所以允许空")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + completedTimeMonth_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + completedTimeMonth_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String completedTimeMonth;

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
	@FieldDescribe("创建人组织")
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
	@Index(name = TABLE + IndexNameMiddle + applicationAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String applicationAlias;

	public static final String process_FIELDNAME = "process";
	@FieldDescribe("流程ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + process_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + process_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String process;

	public static final String processName_FIELDNAME = "processName";
	@FieldDescribe("流程名称")
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

	public static final String form_FIELDNAME = "form";
	@FieldDescribe("使用表单ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + form_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + form_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String form;

	public static final String formData_FIELDNAME = "formData";
	@FieldDescribe("文本内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + formData_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String formData;

	public static final String formMobileData_FIELDNAME = "formMobileData";
	@FieldDescribe("移动端文本内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + formMobileData_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String formMobileData;

	public static final String work_FIELDNAME = "work";
	@Flag
	@FieldDescribe("Work Id.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + work_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + work_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String work;

	public static final String expireTime_FIELDNAME = "expireTime";
	@FieldDescribe("任务截止时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + expireTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + expireTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date expireTime;

	public static final String expired_FIELDNAME = "expired";
	@FieldDescribe("是否超时.")
	@Column(name = ColumnNamePrefix + expired_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + expired_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean expired;

	public static final String duration_FIELDNAME = "duration";
	@FieldDescribe("工作时长(分钟数).")
	@Column(name = ColumnNamePrefix + duration_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Long duration;

	public static final String allowRollback_FIELDNAME = "allowRollback";
	@FieldDescribe("完成后是否允许回滚.")
	@Column(name = ColumnNamePrefix + allowRollback_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + allowRollback_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean allowRollback;

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

	public String getCreatorUnit() {
		return creatorUnit;
	}

	public void setCreatorUnit(String creatorUnit) {
		this.creatorUnit = creatorUnit;
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

	public String getCreatorUnitLevelName() {
		return creatorUnitLevelName;
	}

	public void setCreatorUnitLevelName(String creatorUnitLevelName) {
		this.creatorUnitLevelName = creatorUnitLevelName;
	}

	public Boolean getAllowRollback() {
		return allowRollback;
	}

	public void setAllowRollback(Boolean allowRollback) {
		this.allowRollback = allowRollback;
	}

}