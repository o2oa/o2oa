package com.x.attendance.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.StatisticPersonForMonth.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.StatisticPersonForMonth.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class StatisticPersonForMonth extends SliceJpaObject {

	private static final long serialVersionUID = 2751187045074892095L;
	private static final String TABLE = PersistenceProperties.StatisticPersonForMonth.table;

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
		String employeeName = null;
		if (this.employeeName != null) {
			employeeName = this.employeeName.split("@")[0];
		} else {
			employeeName = this.employeeName;
		}
		this.setSequence(StringUtils.join(this.statisticYear + this.statisticMonth, employeeName, this.getId()));
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
	@FieldDescribe("员工姓名")
	@Column(name = "xemployeeName", length = JpaObject.length_96B)
	@CheckPersist(allowEmpty = false)
	private String employeeName;

	@FieldDescribe("组织名称")
	@Column(name = "xunitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = false)
	private String unitName;

	@FieldDescribe("顶层组织名称")
	@Column(name = "xtopUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = false)
	private String topUnitName;

	@FieldDescribe("统计年份")
	@Column(name = "xstatisticYear", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = false)
	private String statisticYear;

	@FieldDescribe("统计月份")
	@Column(name = "xstatisticMonth", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = false)
	private String statisticMonth;

	@FieldDescribe("应出勤天数")
	@Column(name = "xworkDayCount")
	private Double workDayCount;

	@FieldDescribe("实际出勤天数")
	@Column(name = "xonDutyDayCount")
	private Double onDutyDayCount;

	@FieldDescribe("缺勤天数")
	@Column(name = "xabsenceDayCount")
	private Double absenceDayCount;

	@FieldDescribe("休假天数")
	@Column(name = "xonSelfHolidayCount")
	private Double onSelfHolidayCount;

	@FieldDescribe("签到次数")
	@Column(name = "xonDutyTimes")
	private Long onDutyTimes;

	@FieldDescribe("签退次数")
	@Column(name = "xoffDutyTimes")
	private Long offDutyTimes;

	@FieldDescribe("迟到次数")
	@Column(name = "xlateTimes")
	private Long lateTimes;

	@FieldDescribe("早退次数")
	@Column(name = "xleaveEarlyTimes")
	private Long leaveEarlyTimes;

	@FieldDescribe("工时不足次数")
	@Column(name = "xlackOfTimeCount")
	private Long lackOfTimeCount;

	@FieldDescribe("异常打卡人数")
	@Column(name = "xabNormalDutyCount")
	private Long abNormalDutyCount;

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getTopUnitName() {
		return topUnitName;
	}

	public void setTopUnitName(String topUnitName) {
		this.topUnitName = topUnitName;
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

	public Double getWorkDayCount() {
		return workDayCount;
	}

	public void setWorkDayCount(Double workDayCount) {
		this.workDayCount = workDayCount;
	}

	public Double getOnDutyDayCount() {
		return onDutyDayCount;
	}

	public void setOnDutyDayCount(Double onDutyDayCount) {
		this.onDutyDayCount = onDutyDayCount;
	}

	public Double getAbsenceDayCount() {
		return absenceDayCount < 0 ? 0 : absenceDayCount;
	}

	public void setAbsenceDayCount(Double absenceDayCount) {
		this.absenceDayCount = absenceDayCount;
	}

	public Double getOnSelfHolidayCount() {
		return onSelfHolidayCount;
	}

	public void setOnSelfHolidayCount(Double onSelfHolidayCount) {
		this.onSelfHolidayCount = onSelfHolidayCount;
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

	public Long getLeaveEarlyTimes() {
		return leaveEarlyTimes;
	}

	public void setLeaveEarlyTimes(Long leaveEarlyTimes) {
		this.leaveEarlyTimes = leaveEarlyTimes;
	}

	public Long getLackOfTimeCount() {
		return lackOfTimeCount;
	}

	public void setLackOfTimeCount(Long lackOfTimeCount) {
		this.lackOfTimeCount = lackOfTimeCount;
	}

	public Long getAbNormalDutyCount() {
		return abNormalDutyCount;
	}

	public void setAbNormalDutyCount(Long abNormalDutyCount) {
		this.abNormalDutyCount = abNormalDutyCount;
	}
}