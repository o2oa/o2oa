package com.x.organization.core.entity.accredit;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.organization.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Empower", description = "组织授权.")
@Entity
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
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

	public static final String TYPE_FILTER = "filter";

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
		// nothing
	}

	/** 更新运行方法 */

	/** flag标志位 */
	/** 默认内容结束 */

	public static final String FROMPERSON_FIELDNAME = "fromPerson";
	@FieldDescribe("人员.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + FROMPERSON_FIELDNAME)
	private String fromPerson;

	public static final String FROMIDENTITY_FIELDNAME = "fromIdentity";
	@FieldDescribe("身份.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + FROMIDENTITY_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + FROMIDENTITY_FIELDNAME)
	private String fromIdentity;

	public static final String TOPERSON_FIELDNAME = "toPerson";
	@FieldDescribe("委托人员.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + TOPERSON_FIELDNAME)
	private String toPerson;

	public static final String TOIDENTITY_FIELDNAME = "toIdentity";
	@FieldDescribe("委托身份.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + TOIDENTITY_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + TOIDENTITY_FIELDNAME)
	private String toIdentity;

	public static final String APPLICATION_FIELDNAME = "application";
	@FieldDescribe("应用.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + APPLICATION_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + APPLICATION_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String application;

	public static final String APPLICATIONNAME_FIELDNAME = "applicationName";
	@FieldDescribe("应用名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + APPLICATIONNAME_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String applicationName;

	public static final String APPLICATIONALIAS_FIELDNAME = "applicationAlias";
	@FieldDescribe("应用别名.")
	@Column(length = length_255B, name = ColumnNamePrefix + APPLICATIONALIAS_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String applicationAlias;

	public static final String EDITION_FIELDNAME = "edition";
	@FieldDescribe("流程版本.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + EDITION_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + EDITION_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String edition;

	public static final String PROCESS_FIELDNAME = "process";
	@FieldDescribe("流程ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + PROCESS_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + PROCESS_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String process;

	public static final String PROCESSNAME_FIELDNAME = "processName";
	@FieldDescribe("流程名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + PROCESSNAME_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processName;

	public static final String PROCESSALIAS_FIELDNAME = "processAlias";
	@FieldDescribe("流程别名.")
	@Column(length = length_255B, name = ColumnNamePrefix + PROCESSALIAS_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processAlias;

	public static final String FILTERLISTDATA_FIELDNAME = "filterListData";
	@FieldDescribe("过滤条件.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + FILTERLISTDATA_FIELDNAME)
	private String filterListData;

	public static final String TYPE_FIELDNAME = "type";
	@FieldDescribe("授权类型:all,application,process,filter.")
	@Column(name = ColumnNamePrefix + TYPE_FIELDNAME, length = length_32B)
	@CheckPersist(allowEmpty = false)
	private String type;

	public static final String STARTTIME_FIELDNAME = "startTime";
	@FieldDescribe("授权开始时间.")
	@Column(name = ColumnNamePrefix + STARTTIME_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + STARTTIME_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date startTime;

	public static final String COMPLETEDTIME_FIELDNAME = "completedTime";
	@FieldDescribe("授权结束时间.")
	@Column(name = ColumnNamePrefix + COMPLETEDTIME_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + COMPLETEDTIME_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date completedTime;

	public static final String ENABLE_FIELDNAME = "enable";
	@FieldDescribe("是否启用.")
	@Column(name = ColumnNamePrefix + ENABLE_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean enable;

	public static final String KEEPENABLE_FIELDNAME = "keepEnable";
	@FieldDescribe("是否保留我的待办.")
	@Column(name = ColumnNamePrefix + KEEPENABLE_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean keepEnable;

	public Boolean getKeepEnable() {
		return keepEnable;
	}

	public void setKeepEnable(Boolean keepEnable) {
		this.keepEnable = keepEnable;
	}

	public String getFilterListData() {
		return filterListData;
	}

	public void setFilterListData(String filterListData) {
		this.filterListData = filterListData;
	}

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

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

}