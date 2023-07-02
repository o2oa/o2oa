package com.x.attendance.entity;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AttendanceStatisticRequireLog", description = "考勤统计需求日志.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceStatisticRequireLog.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.AttendanceStatisticRequireLog.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceStatisticRequireLog extends SliceJpaObject {

	private static final long serialVersionUID = 7681675551507448290L;
	private static final String TABLE = PersistenceProperties.AttendanceStatisticRequireLog.table;

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
	public static final String statisticName_FIELDNAME = "statisticName";
	@FieldDescribe("统计名称")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + statisticName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String statisticName = "";

	public static final String statisticType_FIELDNAME = "statisticType";
	@FieldDescribe("统计类型:PERSON_PER_MONTH|UNIT_PER_MONTH|TOPUNIT_PER_MONTH|UNIT_PER_DAY|TOPUNIT_PER_DAY")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + statisticType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String statisticType = "";

	public static final String statisticKey_FIELDNAME = "statisticKey";
	@FieldDescribe("统计键值")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + statisticKey_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String statisticKey = "";

	public static final String statisticYear_FIELDNAME = "statisticYear";
	@FieldDescribe("统计年月")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + statisticYear_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String statisticYear = "";

	public static final String statisticMonth_FIELDNAME = "statisticMonth";
	@FieldDescribe("统计月份")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + statisticMonth_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String statisticMonth = "";

	public static final String statisticDay_FIELDNAME = "statisticDay";
	@FieldDescribe("统计日期")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + statisticDay_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String statisticDay = "";

	public static final String processTime_FIELDNAME = "processTime";
	@FieldDescribe("处理时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + processTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date processTime = null;

	public static final String processStatus_FIELDNAME = "processStatus";
	@FieldDescribe("处理状态")
	@Column(name = ColumnNamePrefix + processStatus_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processStatus = "WAITING";

	public static final String description_FIELDNAME = "description";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("说明备注")
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description = "";

	public String getStatisticName() {
		return statisticName;
	}

	public void setStatisticName(String statisticName) {
		this.statisticName = statisticName;
	}

	/**
	 * 统计类型:PERSON_PER_MONTH|UNIT_PER_MONTH|TOPUNIT_PER_MONTH|UNIT_PER_DAY|TOPUNIT_PER_DAY
	 * 
	 * @return
	 */
	public String getStatisticType() {
		return statisticType;
	}

	/**
	 * 统计类型:PERSON_PER_MONTH|UNIT_PER_MONTH|TOPUNIT_PER_MONTH|UNIT_PER_DAY|TOPUNIT_PER_DAY
	 * 
	 * @param statisticType
	 */
	public void setStatisticType(String statisticType) {
		this.statisticType = statisticType;
	}

	public String getStatisticKey() {
		return statisticKey;
	}

	public void setStatisticKey(String statisticKey) {
		this.statisticKey = statisticKey;
	}

	public Date getProcessTime() {
		return processTime;
	}

	public void setProcessTime(Date processTime) {
		this.processTime = processTime;
	}

	/**
	 * WAITING|PROCESSING|COMPLETE|ERROR
	 * 
	 * @return
	 */
	public String getProcessStatus() {
		return processStatus;
	}

	/**
	 * WAITING|PROCESSING|COMPLETE|ERROR
	 * 
	 * @param processStatus
	 */
	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatisticYear() {
		return statisticYear;
	}

	public void setStatisticYear(String statisticYear) {
		this.statisticYear = statisticYear;
	}

	public String getStatisticMonth() {
		return statisticMonth;
	}

	public void setStatisticMonth(String statisticMonth) {
		this.statisticMonth = statisticMonth;
	}

	public String getStatisticDay() {
		return statisticDay;
	}

	public void setStatisticDay(String statisticDay) {
		this.statisticDay = statisticDay;
	}
}