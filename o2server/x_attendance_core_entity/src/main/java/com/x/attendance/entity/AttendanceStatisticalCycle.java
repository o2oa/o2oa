package com.x.attendance.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AttendanceStatisticalCycle", description = "考勤统计回收.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceStatisticalCycle.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.AttendanceStatisticalCycle.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceStatisticalCycle extends SliceJpaObject {

	private static final long serialVersionUID = 3253441204302907992L;
	private static final String TABLE = PersistenceProperties.AttendanceStatisticalCycle.table;

	public AttendanceStatisticalCycle() {
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
	public static final String topUnitName_FIELDNAME = "topUnitName";
	@FieldDescribe("顶层组织名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ topUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String topUnitName;

	public static final String unitName_FIELDNAME = "unitName";
	@FieldDescribe("组织名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ unitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String unitName;

	public static final String cycleYear_FIELDNAME = "cycleYear";
	@FieldDescribe("统计周期年份")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + cycleYear_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String cycleYear;

	public static final String cycleMonth_FIELDNAME = "cycleMonth";
	@FieldDescribe("统计周期月份")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + cycleMonth_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String cycleMonth;

	public static final String cycleStartDateString_FIELDNAME = "cycleStartDateString";
	@FieldDescribe("月周期开始日期")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + cycleStartDateString_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String cycleStartDateString;

	public static final String cycleEndDateString_FIELDNAME = "cycleEndDateString";
	@FieldDescribe("月周期结束日期")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + cycleEndDateString_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String cycleEndDateString;

	public static final String cycleStartDate_FIELDNAME = "cycleStartDate";
	@FieldDescribe("月周期开始日期")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + cycleStartDate_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date cycleStartDate;

	public static final String cycleEndDate_FIELDNAME = "cycleEndDate";
	@FieldDescribe("月周期结束日期")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + cycleEndDate_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date cycleEndDate;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("说明备注")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public String getTopUnitName() {
		return topUnitName;
	}

	public void setTopUnitName(String topUnitName) {
		this.topUnitName = topUnitName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getCycleYear() {
		return cycleYear;
	}

	public void setCycleYear(String cycleYear) {
		this.cycleYear = cycleYear;
	}

	public String getCycleMonth() {
		return cycleMonth;
	}

	public void setCycleMonth(String cycleMonth) {
		this.cycleMonth = cycleMonth;
	}

	public String getCycleStartDateString() {
		return cycleStartDateString;
	}

	public void setCycleStartDateString(String cycleStartDateString) {
		this.cycleStartDateString = cycleStartDateString;
	}

	public String getCycleEndDateString() {
		return cycleEndDateString;
	}

	public void setCycleEndDateString(String cycleEndDateString) {
		this.cycleEndDateString = cycleEndDateString;
	}

	public Date getCycleStartDate() {
		if (cycleStartDate == null) {
			if (cycleStartDateString != null) {
				try {
					return (new DateOperation()).getDateFromString(cycleStartDateString);
				} catch (Exception e) {
					return null;
				}
			}
		}
		return cycleStartDate;
	}

	public void setCycleStartDate(Date cycleStartDate) {
		this.cycleStartDate = cycleStartDate;
	}

	public Date getCycleEndDate() {
		if (cycleEndDate == null) {
			if (cycleEndDateString != null) {
				try {
					return (new DateOperation()).getDateFromString(cycleEndDateString);
				} catch (Exception e) {
					return null;
				}
			}
		}
		return cycleEndDate;
	}

	public void setCycleEndDate(Date cycleEndDate) {
		this.cycleEndDate = cycleEndDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}