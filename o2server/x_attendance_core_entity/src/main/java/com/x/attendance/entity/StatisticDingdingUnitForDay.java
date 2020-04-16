package com.x.attendance.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import javax.persistence.*;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.StatisticDingdingUnitForDay.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.StatisticDingdingUnitForDay.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class StatisticDingdingUnitForDay extends SliceJpaObject {



	private static final String TABLE = PersistenceProperties.StatisticDingdingUnitForDay.table;
	private static final long serialVersionUID = 2090817422412907325L;

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

	@FieldDescribe("O2用户所在的组织")
	@Column(name = ColumnNamePrefix + "o2Unit", length = length_128B)
	@CheckPersist(allowEmpty = false)
	private String o2Unit;

	@FieldDescribe("统计年份")
	@Column(name = "xstatisticYear", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = false)
	private String statisticYear;

	@FieldDescribe("统计月份")
	@Column(name = "xstatisticMonth", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = false)
	private String statisticMonth;

	@FieldDescribe("统计日期")
	@Column(name = "xstatisticDate", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = false)
	private String statisticDate;

	@FieldDescribe("出勤人数")
	@Column(name = "xworkDayCount")
	private Long workDayCount;

	@FieldDescribe("上班签到人数")
	@Column(name = "xonDutyTimes")
	private Long onDutyTimes;

	@FieldDescribe("下班签到人数")
	@Column(name = "xoffDutyTimes")
	private Long offDutyTimes;


	@FieldDescribe("正常签到次数")
	@Column(name = "xresultNormal")
	private Long resultNormal;

	@FieldDescribe("迟到人数")
	@Column(name = "xlateTimes")
	private Long lateTimes;

	@FieldDescribe("严重迟到人数")
	@Column(name = "xSeriousLateTimes")
	private Long seriousLateTimes;

	@FieldDescribe("早退人数")
	@Column(name = "xleaveEarlyTimes")
	private Long leaveEarlyTimes;

	@FieldDescribe("旷工人数")
	@Column(name = "xAbsenteeismTimes")
	private Long absenteeismTimes;

	@FieldDescribe("未打卡人数")
	@Column(name = "xNotSignedCount")
	private Long notSignedCount;


	public Long getResultNormal() {
		return resultNormal;
	}

	public void setResultNormal(Long resultNormal) {
		this.resultNormal = resultNormal;
	}

	public String getStatisticDate() {
		return statisticDate;
	}

	public void setStatisticDate(String statisticDate) {
		this.statisticDate = statisticDate;
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

	public Long getSeriousLateTimes() {
		return seriousLateTimes;
	}

	public void setSeriousLateTimes(Long seriousLateTimes) {
		this.seriousLateTimes = seriousLateTimes;
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