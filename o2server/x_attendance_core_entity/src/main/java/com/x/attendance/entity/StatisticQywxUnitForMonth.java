package com.x.attendance.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "StatisticQywxUnitForMonth", description = "考勤企业微信组织按月统计.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.StatisticQywxUnitForMonth.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.StatisticQywxUnitForMonth.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class StatisticQywxUnitForMonth extends SliceJpaObject {

	private static final String TABLE = PersistenceProperties.StatisticQywxUnitForMonth.table;
	private static final long serialVersionUID = 2831416127767736230L;

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
	public static final String o2Unit_FIELDNAME = "o2Unit";
	@FieldDescribe("O2用户所在的组织")
	@Column(length = length_128B, name = ColumnNamePrefix + o2Unit_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String o2Unit;

	public static final String statisticYear_FIELDNAME = "statisticYear";
	@FieldDescribe("统计年份")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + statisticYear_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String statisticYear;

	public static final String statisticMonth_FIELDNAME = "statisticMonth";
	@FieldDescribe("统计月份")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + statisticMonth_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String statisticMonth;

	public static final String workDayCount_FIELDNAME = "workDayCount";
	@FieldDescribe("出勤天数")
	@Column(name = ColumnNamePrefix + workDayCount_FIELDNAME)
	private Long workDayCount;

	public static final String onDutyTimes_FIELDNAME = "onDutyTimes";
	@FieldDescribe("上班签到次数")
	@Column(name = ColumnNamePrefix + onDutyTimes_FIELDNAME)
	private Long onDutyTimes;

	public static final String offDutyTimes_FIELDNAME = "offDutyTimes";
	@FieldDescribe("下班签到次数")
	@Column(name = ColumnNamePrefix + offDutyTimes_FIELDNAME)
	private Long offDutyTimes;

	public static final String outsideDutyTimes_FIELDNAME = "outsideDutyTimes";
	@FieldDescribe("外出签到次数")
	@Column(name = ColumnNamePrefix + outsideDutyTimes_FIELDNAME)
	private Long outsideDutyTimes;

	public static final String resultNormal_FIELDNAME = "resultNormal";
	@FieldDescribe("正常签到次数")
	@Column(name = ColumnNamePrefix + resultNormal_FIELDNAME)
	private Long resultNormal;

	public static final String lateTimes_FIELDNAME = "lateTimes";
	@FieldDescribe("迟到次数")
	@Column(name = ColumnNamePrefix + lateTimes_FIELDNAME)
	private Long lateTimes;

	public static final String leaveEarlyTimes_FIELDNAME = "leaveEarlyTimes";
	@FieldDescribe("早退次数")
	@Column(name = ColumnNamePrefix + leaveEarlyTimes_FIELDNAME)
	private Long leaveEarlyTimes;

	public static final String absenteeismTimes_FIELDNAME = "absenteeismTimes";
	@FieldDescribe("旷工次数")
	@Column(name = ColumnNamePrefix + absenteeismTimes_FIELDNAME)
	private Long absenteeismTimes;

	public static final String notSignedCount_FIELDNAME = "notSignedCount";
	@FieldDescribe("未打卡次数")
	@Column(name = ColumnNamePrefix + notSignedCount_FIELDNAME)
	private Long notSignedCount;

	public Long getResultNormal() {
		return resultNormal;
	}

	public void setResultNormal(Long resultNormal) {
		this.resultNormal = resultNormal;
	}

	public String getO2Unit() {
		return o2Unit;
	}

	public void setO2Unit(String o2Unit) {
		this.o2Unit = o2Unit;
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

	public Long getWorkDayCount() {
		return workDayCount;
	}

	public void setWorkDayCount(Long workDayCount) {
		this.workDayCount = workDayCount;
	}

	public Long getOnDutyTimes() {
		return onDutyTimes;
	}

	public void setOnDutyTimes(Long onDutyTimes) {
		this.onDutyTimes = onDutyTimes;
	}

	public Long getOffDutyTimes() {
		return offDutyTimes;
	}

	public void setOffDutyTimes(Long offDutyTimes) {
		this.offDutyTimes = offDutyTimes;
	}

	public Long getLateTimes() {
		return lateTimes;
	}

	public void setLateTimes(Long lateTimes) {
		this.lateTimes = lateTimes;
	}

	public Long getOutsideDutyTimes() {
		return outsideDutyTimes;
	}

	public void setOutsideDutyTimes(Long outsideDutyTimes) {
		this.outsideDutyTimes = outsideDutyTimes;
	}

	public Long getLeaveEarlyTimes() {
		return leaveEarlyTimes;
	}

	public void setLeaveEarlyTimes(Long leaveEarlyTimes) {
		this.leaveEarlyTimes = leaveEarlyTimes;
	}

	public Long getAbsenteeismTimes() {
		return absenteeismTimes;
	}

	public void setAbsenteeismTimes(Long absenteeismTimes) {
		this.absenteeismTimes = absenteeismTimes;
	}

	public Long getNotSignedCount() {
		return notSignedCount;
	}

	public void setNotSignedCount(Long notSignedCount) {
		this.notSignedCount = notSignedCount;
	}
}