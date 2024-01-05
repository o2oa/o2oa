package com.x.program.center.core.entity;

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

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(name = "ScheduleLog", description = "服务管理日志记录.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.log, reference = ContainerEntity.Reference.soft)
@Table(name = PersistenceProperties.ScheduleLog.TABLE, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.ScheduleLog.TABLE + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ScheduleLog extends SliceJpaObject {

	private static final long serialVersionUID = -3922384457908855834L;

	private static final String TABLE = PersistenceProperties.ScheduleLog.TABLE;

	public ScheduleLog() {

	}

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

	/* 更新运行方法 */

	public static final String node_FIELDNAME = "node";
	@FieldDescribe("运行的服务器.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + node_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + node_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String node;

	public static final String application_FIELDNAME = "application";
	@FieldDescribe("运行的应用.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + application_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + application_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String application;

	public static final String className_FIELDNAME = "className";
	@FieldDescribe("运行的className")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + className_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + className_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String className;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("类型schedule/scheduleOnLocal")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + type_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + type_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String type;

	public static final String fireTime_FIELDNAME = "fireTime";
	@FieldDescribe("运行结束时间.")
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + fireTime_FIELDNAME)
	@Column(name = ColumnNamePrefix + fireTime_FIELDNAME)
	private Date fireTime;

	public static final String elapsed_FIELDNAME = "elapsed";
	@FieldDescribe("运行时长(毫秒).")
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + elapsed_FIELDNAME)
	@Column(name = ColumnNamePrefix + elapsed_FIELDNAME)
	private Long elapsed;

	public static final String stackTrace_FIELDNAME = "stackTrace";
	@FieldDescribe("错误堆栈.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + stackTrace_FIELDNAME)
	private String stackTrace;

	public static final String success_FIELDNAME = "success";
	@FieldDescribe("运行成功.")
	@Column(name = ColumnNamePrefix + success_FIELDNAME)
	private Boolean success;

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getFireTime() {
		return fireTime;
	}

	public void setFireTime(Date fireTime) {
		this.fireTime = fireTime;
	}

	public Long getElapsed() {
		return elapsed;
	}

	public void setElapsed(Long elapsed) {
		this.elapsed = elapsed;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

}