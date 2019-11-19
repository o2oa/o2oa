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
@Table(name = PersistenceProperties.StatisticTopUnitForDay.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.StatisticTopUnitForDay.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class StatisticTopUnitForDay extends SliceJpaObject {

	private static final long serialVersionUID = 5211190018798042409L;
	private static final String TABLE = PersistenceProperties.StatisticTopUnitForDay.table;

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
		String topUnitName = null;
		if (this.topUnitName != null) {
			topUnitName = this.topUnitName.split("@")[0];
		} else {
			topUnitName = this.topUnitName;
		}
		this.setSequence(StringUtils.join(this.statisticDate, topUnitName, this.getId()));
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

	@FieldDescribe("统计日期")
	@Column(name = "xstatisticDate", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = false)
	private String statisticDate;

	@FieldDescribe("应出勤人数")
	@Column(name = "xemployeeCount")
	private Double employeeCount;

	@FieldDescribe("实际出勤人数")
	@Column(name = "xonDutyEmployeeCount")
	private Double onDutyEmployeeCount;

	@FieldDescribe("缺勤人数")
	@Column(name = "xabsenceDayCount")
	private Double absenceDayCount;

	@FieldDescribe("休假人数")
	@Column(name = "xonSelfHolidayEmployeeCount")
	private Double onSelfHolidayEmployeeCount;

	@FieldDescribe("签到人数")
	@Column(name = "xonDutyCount")
	private Long onDutyCount;

	@FieldDescribe("签退人数")
	@Column(name = "xoffDutyCount")
	private Long offDutyCount;

	@FieldDescribe("迟到人数")
	@Column(name = "xlateCount")
	private Long lateCount;

	@FieldDescribe("早退人数")
	@Column(name = "xleaveEarlyCount")
	private Long leaveEarlyCount;

	@FieldDescribe("工时不足人数")
	@Column(name = "xlackOfTimeCount")
	private Long lackOfTimeCount;

	@FieldDescribe("异常打卡人数")
	@Column(name = "xabNormalDutyCount")
	private Long abNormalDutyCount;

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

	public String getStatisticDate() {
		return statisticDate;
	}

	public void setStatisticDate(String statisticDate) {
		this.statisticDate = statisticDate;
	}

	public Double getEmployeeCount() {
		return employeeCount;
	}

	public void setEmployeeCount(Double employeeCount) {
		this.employeeCount = employeeCount;
	}

	public Double getOnDutyEmployeeCount() {
		return onDutyEmployeeCount;
	}

	public void setOnDutyEmployeeCount(Double onDutyEmployeeCount) {
		this.onDutyEmployeeCount = onDutyEmployeeCount;
	}

	public Double getAbsenceDayCount() {
		return absenceDayCount;
	}

	public void setAbsenceDayCount(Double absenceDayCount) {
		this.absenceDayCount = absenceDayCount;
	}

	public Double getOnSelfHolidayEmployeeCount() {
		return onSelfHolidayEmployeeCount;
	}

	public void setOnSelfHolidayEmployeeCount(Double onSelfHolidayEmployeeCount) {
		this.onSelfHolidayEmployeeCount = onSelfHolidayEmployeeCount;
	}

	public Long getOnDutyCount() {
		return onDutyCount;
	}

	public void setOnDutyCount(Long onDutyCount) {
		this.onDutyCount = onDutyCount;
	}

	public Long getOffDutyCount() {
		return offDutyCount;
	}

	public void setOffDutyCount(Long offDutyCount) {
		this.offDutyCount = offDutyCount;
	}

	public Long getLateCount() {
		return lateCount;
	}

	public void setLateCount(Long lateCount) {
		this.lateCount = lateCount;
	}

	public Long getLeaveEarlyCount() {
		return leaveEarlyCount;
	}

	public void setLeaveEarlyCount(Long leaveEarlyCount) {
		this.leaveEarlyCount = leaveEarlyCount;
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