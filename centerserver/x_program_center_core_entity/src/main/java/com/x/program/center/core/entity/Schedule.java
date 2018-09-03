package com.x.program.center.core.entity;

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

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Schedule.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Schedule.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Schedule extends SliceJpaObject {

	private static final long serialVersionUID = 6387104721461689291L;

	public Schedule() {

	}

	public Schedule(String className, String application, String cron) {
		this.application = application;
		this.className = className;
		this.cron = cron;
	}

	private static final String TABLE = PersistenceProperties.Schedule.table;

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
		if (null == this.reportTime) {
			this.reportTime = new Date();
		}
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
	@FieldDescribe("运行的class")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + className_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + className_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String className;

	public static final String cron_FIELDNAME = "cron";
	@FieldDescribe("定时任务corn表达式.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + cron_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + cron_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String cron;

	public static final String fireTime_FIELDNAME = "fireTime";
	@FieldDescribe("指定的运行时间.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + fireTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + fireTime_FIELDNAME)
	private Date fireTime;

	public static final String elapsed_FIELDNAME = "elapsed";
	@FieldDescribe("运行时长(毫秒).")
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + elapsed_FIELDNAME)
	@Column(name = ColumnNamePrefix + elapsed_FIELDNAME)
	private Long elapsed;

	public static final String reportTime_FIELDNAME = "reportTime";
	@FieldDescribe("报告时间.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + reportTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + reportTime_FIELDNAME)
	private Date reportTime;

	// public static String[] FLAGS = new String[] { "id" };

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public Date getFireTime() {
		return fireTime;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public void setFireTime(Date fireTime) {
		this.fireTime = fireTime;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Long getElapsed() {
		return elapsed;
	}

	public void setElapsed(Long elapsed) {
		this.elapsed = elapsed;
	}

	public Date getReportTime() {
		return reportTime;
	}

	public void setReportTime(Date reportTime) {
		this.reportTime = reportTime;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

}