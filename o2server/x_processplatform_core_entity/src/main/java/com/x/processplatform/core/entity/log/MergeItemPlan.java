package com.x.processplatform.core.entity.log;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.PersistenceProperties;

@Entity
@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.log, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Log.MergeItemPlan.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Log.MergeItemPlan.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class MergeItemPlan extends SliceJpaObject {

	private static final long serialVersionUID = -7520516033901189347L;
	private static final String TABLE = PersistenceProperties.Log.MergeItemPlan.table;

	public static final String STATUS_MERGING = "merging";

	public static final String STATUS_COMPLETED = "completed";

	public static final Boolean DEFAULT_ENABLE = false;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	@Override
	public void onPersist() throws Exception {
	}

	@PostLoad
	public void postLoad() {

	}

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String name;

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("状态")
	@Column(length = length_32B, name = ColumnNamePrefix + status_FIELDNAME)
	private String status = STATUS_MERGING;

	public static final String lastUpdatePerson_FIELDNAME = "lastUpdatePerson";
	@FieldDescribe("最后更新者")
	@CheckPersist(allowEmpty = false)
	@Column(length = length_255B, name = ColumnNamePrefix + lastUpdatePerson_FIELDNAME)
	private String lastUpdatePerson;

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("应用的最后修改时间。")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	private Date lastUpdateTime;

	public static final String application_FIELDNAME = "application";
	@FieldDescribe("应用标识")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + application_FIELDNAME)
	private String application;

	public static final String applicationName_FIELDNAME = "applicationName";
	@FieldDescribe("应用名称")
	@Column(length = length_255B, name = ColumnNamePrefix + applicationName_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = false)
	private String applicationName;

	public static final String process_FIELDNAME = "process";
	@FieldDescribe("流程标识")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + process_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + process_FIELDNAME)
	private String process;

	public static final String processName_FIELDNAME = "processName";
	@FieldDescribe("流程名称")
	@Column(length = length_255B, name = ColumnNamePrefix + processName_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = false)
	private String processName;

	public static final String startTime_FIELDNAME = "startTime";
	@FieldDescribe("开始时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + startTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + startTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date startTime;

	public static final String completedTime_FIELDNAME = "completedTime";
	@FieldDescribe("结束时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + completedTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + completedTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date completedTime;

	public static final String count_FIELDNAME = "count";
	@FieldDescribe("数量")
	@Column(name = ColumnNamePrefix + count_FIELDNAME)
	private Long count;

	public static final String estimatedCount_FIELDNAME = "estimatedCount";
	@FieldDescribe("估计数量")
	@Column(name = ColumnNamePrefix + estimatedCount_FIELDNAME)
	private Long estimatedCount;

	public static final String enable_FIELDNAME = "enable";
	@FieldDescribe("是否启用")
	@Column(name = ColumnNamePrefix + enable_FIELDNAME)
	private Boolean enable = DEFAULT_ENABLE;

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLastUpdatePerson() {
		return lastUpdatePerson;
	}

	public void setLastUpdatePerson(String lastUpdatePerson) {
		this.lastUpdatePerson = lastUpdatePerson;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
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

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Long getEstimatedCount() {
		return estimatedCount;
	}

	public void setEstimatedCount(Long estimatedCount) {
		this.estimatedCount = estimatedCount;
	}

}
