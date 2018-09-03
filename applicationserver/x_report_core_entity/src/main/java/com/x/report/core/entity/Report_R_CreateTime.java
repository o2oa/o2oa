package com.x.report.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Report_R_CreateTime.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Report_R_CreateTime.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Report_R_CreateTime extends SliceJpaObject {

	private static final long serialVersionUID = 1325197931747463979L;
	private static final String TABLE = PersistenceProperties.Report_R_CreateTime.table;

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

	@FieldDescribe("创建时间,自动生成.")
	@Column(name = ColumnNamePrefix + createTime_FIELDNAME)
	private Date createTime;

	@FieldDescribe("修改时间,自动生成.")
	@Column(name = ColumnNamePrefix + updateTime_FIELDNAME)
	private Date updateTime;

	@FieldDescribe("列表序号,由创建时间以及ID组成.在保存时自动生成.")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + sequence_FIELDNAME)
	private String sequence;

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	public void onPersist() throws Exception {
	}
	/*
	 * =============================================================================
	 * ===== 以上为 JpaObject 默认字段
	 * =============================================================================
	 * =====
	 */

	/*
	 * =============================================================================
	 * ===== 以下为具体不同的业务及数据表字段要求
	 * =============================================================================
	 * =====
	 */
	@FieldDescribe("上一次月汇报时间")
	@Column(name = "xlastMonthReportTime")
	private Date lastMonthReportTime;

	@FieldDescribe("上一次月汇报ID")
	@Column(name = "xlastMonthReportId", length = JpaObject.length_id)
	private String lastMonthReportId;

	@FieldDescribe("下一次月汇报时间")
	@Column(name = "xnextMonthReportTime")
	private Date nextMonthReportTime;

	@FieldDescribe("上一次周汇报时间")
	@Column(name = "xlastWeekReportTime")
	private Date lastWeekReportTime;

	@FieldDescribe("上一次周汇报ID")
	@Column(name = "xlastWeekReportId", length = JpaObject.length_id)
	private String lastWeekReportId;

	@FieldDescribe("下一次周汇报时间")
	@Column(name = "xnextWeekReportTime")
	private Date nextWeekReportTime;

	public static final String lastDayReportTime_FIELDNAME = "lastDayReportTime";
	@FieldDescribe("上一次日汇报时间")
	@Column(name = ColumnNamePrefix + lastDayReportTime_FIELDNAME)
	private Date lastDayReportTime;

	@FieldDescribe("上一次日汇报ID")
	@Column(name = "xlastDayReportId", length = JpaObject.length_id)
	private String lastDayReportId;

	public static final String nextDayReportTime_FIELDNAME = "nextDayReportTime";
	@FieldDescribe("下一次日汇报时间")
	@Column(name = ColumnNamePrefix + nextDayReportTime_FIELDNAME)
	private Date nextDayReportTime;

	public Date getLastMonthReportTime() {
		return lastMonthReportTime;
	}

	public Date getNextMonthReportTime() {
		return nextMonthReportTime;
	}

	public Date getLastWeekReportTime() {
		return lastWeekReportTime;
	}

	public Date getNextWeekReportTime() {
		return nextWeekReportTime;
	}

	public void setLastMonthReportTime(Date lastMonthReportTime) {
		this.lastMonthReportTime = lastMonthReportTime;
	}

	public void setNextMonthReportTime(Date nextMonthReportTime) {
		this.nextMonthReportTime = nextMonthReportTime;
	}

	public void setLastWeekReportTime(Date lastWeekReportTime) {
		this.lastWeekReportTime = lastWeekReportTime;
	}

	public void setNextWeekReportTime(Date nextWeekReportTime) {
		this.nextWeekReportTime = nextWeekReportTime;
	}

	public String getLastMonthReportId() {
		return lastMonthReportId;
	}

	public String getLastWeekReportId() {
		return lastWeekReportId;
	}

	public Date getLastDayReportTime() {
		return lastDayReportTime;
	}

	public String getLastDayReportId() {
		return lastDayReportId;
	}

	public Date getNextDayReportTime() {
		return nextDayReportTime;
	}

	public void setLastMonthReportId(String lastMonthReportId) {
		this.lastMonthReportId = lastMonthReportId;
	}

	public void setLastWeekReportId(String lastWeekReportId) {
		this.lastWeekReportId = lastWeekReportId;
	}

	public void setLastDayReportTime(Date lastDayReportTime) {
		this.lastDayReportTime = lastDayReportTime;
	}

	public void setLastDayReportId(String lastDayReportId) {
		this.lastDayReportId = lastDayReportId;
	}

	public void setNextDayReportTime(Date nextDayReportTime) {
		this.nextDayReportTime = nextDayReportTime;
	}

}