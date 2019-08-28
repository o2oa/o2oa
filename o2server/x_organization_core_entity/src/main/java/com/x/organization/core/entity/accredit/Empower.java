package com.x.organization.core.entity.accredit;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.organization.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Accredit.Empower.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Accredit.Empower.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Empower extends SliceJpaObject {

	private static final long serialVersionUID = -6518383742766451210L;

	private static final String TABLE = PersistenceProperties.Accredit.Empower.table;

	public static final String TYPE_ALL = "all";

	public static final String TYPE_APPLICATION = "application";

	public static final String TYPE_PROCESS = "process";

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

	/** 更新运行方法 */

	/** flag标志位 */
	/** 默认内容结束 */

	public static final String fromPerson_FIELDNAME = "fromPerson";
	@FieldDescribe("人员.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + fromPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + fromPerson_FIELDNAME)
	private String fromPerson;

	public static final String fromIdentity_FIELDNAME = "fromIdentity";
	@FieldDescribe("身份.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + fromIdentity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + fromIdentity_FIELDNAME)
	private String fromIdentity;

	public static final String toPerson_FIELDNAME = "toPerson";
	@FieldDescribe("委托人员.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + toPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + toPerson_FIELDNAME)
	private String toPerson;

	public static final String toIdentity_FIELDNAME = "toIdentity";
	@FieldDescribe("委托身份.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + toIdentity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + toIdentity_FIELDNAME)
	private String toIdentity;

	public static final String application_FIELDNAME = "application";
	@FieldDescribe("应用.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + application_FIELDNAME)
	@CheckPersist(allowEmpty = true)
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
	@Index(name = TABLE + IndexNameMiddle + processAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processAlias;

//	public static final String whole_FIELDNAME = "whole";
//	@FieldDescribe("全部进行授权,如果启用全部授权,那么指定身份的待办全部进行授权.")
//	@Column(name = ColumnNamePrefix + whole_FIELDNAME)
//	@Index(name = TABLE + IndexNameMiddle + whole_FIELDNAME)
//	@CheckPersist(allowEmpty = false)
//	private Boolean whole;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("授权类型:all,application,process.")
	@Column(name = ColumnNamePrefix + type_FIELDNAME, length = length_32B)
	@Index(name = TABLE + IndexNameMiddle + type_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String type;

	public static final String startTime_FIELDNAME = "startTime";
	@FieldDescribe("授权开始时间.")
	@Column(name = ColumnNamePrefix + startTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + startTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date startTime;

	public static final String completedTime_FIELDNAME = "completedTime";
	@FieldDescribe("授权结束时间.")
	@Column(name = ColumnNamePrefix + completedTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + completedTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date completedTime;

	public static final String enable_FIELDNAME = "enable";
	@FieldDescribe("是否启用.")
	@Column(name = ColumnNamePrefix + enable_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + enable_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean enable;

	public String getFromPerson() {
		return fromPerson;
	}

	public void setFromPerson(String fromPerson) {
		this.fromPerson = fromPerson;
	}

	public String getFromIdentity() {
		return fromIdentity;
	}

	public void setFromIdentity(String fromIdentity) {
		this.fromIdentity = fromIdentity;
	}

	public String getToPerson() {
		return toPerson;
	}

	public void setToPerson(String toPerson) {
		this.toPerson = toPerson;
	}

	public String getToIdentity() {
		return toIdentity;
	}

	public void setToIdentity(String toIdentity) {
		this.toIdentity = toIdentity;
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

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getCompletedTime() {
		return completedTime;
	}

	public void setCompletedTime(Date completedTime) {
		this.completedTime = completedTime;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}