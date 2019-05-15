package com.x.organization.core.entity.accredit;

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
@Table(name = PersistenceProperties.Accredit.Trust.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Accredit.Trust.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Trust extends SliceJpaObject {

	private static final long serialVersionUID = -6518383742766451210L;

	private static final String TABLE = PersistenceProperties.Accredit.Trust.table;

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

	public static final String whole_FIELDNAME = "whole";
	@FieldDescribe("全部进行授权.")
	@Column(name = ColumnNamePrefix + whole_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + whole_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean whole;

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

	public Boolean getWhole() {
		return whole;
	}

	public void setWhole(Boolean whole) {
		this.whole = whole;
	}
}