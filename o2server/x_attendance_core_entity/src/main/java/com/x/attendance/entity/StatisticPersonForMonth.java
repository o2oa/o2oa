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

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "StatisticPersonForMonth", description = "考勤人员按月统计.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
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
	public static final String employeeName_FIELDNAME = "employeeName";
	@FieldDescribe("员工姓名")
	@Column( length = JpaObject.length_96B, name = ColumnNamePrefix + employeeName_FIELDNAME )
	@CheckPersist(allowEmpty = false)
	private String employeeName;

	public static final String unitName_FIELDNAME = "unitName";
	@FieldDescribe("组织名称")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + unitName_FIELDNAME )
	@CheckPersist(allowEmpty = false)
	private String unitName;

	public static final String topUnitName_FIELDNAME = "topUnitName";
	@FieldDescribe("顶层组织名称")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + topUnitName_FIELDNAME )
	@CheckPersist(allowEmpty = false)
	private String topUnitName;

	public static final String statisticYear_FIELDNAME = "statisticYear";
	@FieldDescribe("统计年份")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + statisticYear_FIELDNAME )
	@CheckPersist(allowEmpty = false)
	private String statisticYear;

	public static final String statisticMonth_FIELDNAME = "statisticMonth";
	@FieldDescribe("统计月份")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + statisticMonth_FIELDNAME )
	@CheckPersist(allowEmpty = false)
	private String statisticMonth;

	public static final String workDayCount_FIELDNAME = "workDayCount";
	@FieldDescribe("应出勤天数")
	@Column( name = ColumnNamePrefix + workDayCount_FIELDNAME )
	private Double workDayCount;

	public static final String onDutyDayCount_FIELDNAME = "onDutyDayCount";
	@FieldDescribe("实际出勤天数")
	@Column( name = ColumnNamePrefix + onDutyDayCount_FIELDNAME )
	private Double onDutyDayCount;

	public static final String absenceDayCount_FIELDNAME = "absenceDayCount";
	@FieldDescribe("缺勤天数")
	@Column( name = ColumnNamePrefix + absenceDayCount_FIELDNAME )
	private Double absenceDayCount;

	public static final String onSelfHolidayCount_FIELDNAME = "onSelfHolidayCount";
	@FieldDescribe("休假天数")
	@Column( name = ColumnNamePrefix + onSelfHolidayCount_FIELDNAME )
	private Double onSelfHolidayCount;

	public static final String onDutyTimes_FIELDNAME = "onDutyTimes";
	@FieldDescribe("签到次数")
	@Column( name = ColumnNamePrefix + onDutyTimes_FIELDNAME )
	private Long onDutyTimes;

	public static final String offDutyTimes_FIELDNAME = "offDutyTimes";
	@FieldDescribe("签退次数")
	@Column( name = ColumnNamePrefix + offDutyTimes_FIELDNAME )
	private Long offDutyTimes;

	public static final String lateTimes_FIELDNAME = "lateTimes";
	@FieldDescribe("迟到次数")
	@Column( name = ColumnNamePrefix + lateTimes_FIELDNAME )
	private Long lateTimes;

	public static final String leaveEarlyTimes_FIELDNAME = "leaveEarlyTimes";
	@FieldDescribe("早退次数")
	@Column( name = ColumnNamePrefix + leaveEarlyTimes_FIELDNAME )
	private Long leaveEarlyTimes;

	public static final String lackOfTimeCount_FIELDNAME = "lackOfTimeCount";
	@FieldDescribe("工时不足次数")
	@Column( name = ColumnNamePrefix + lackOfTimeCount_FIELDNAME )
	private Long lackOfTimeCount;

	public static final String abNormalDutyCount_FIELDNAME = "abNormalDutyCount";
	@FieldDescribe("异常打卡人数")
	@Column( name = ColumnNamePrefix + abNormalDutyCount_FIELDNAME )
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