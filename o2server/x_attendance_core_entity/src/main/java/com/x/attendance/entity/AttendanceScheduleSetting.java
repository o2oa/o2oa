package com.x.attendance.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.AttendanceScheduleSetting.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceScheduleSetting.table
		+ JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
				JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
//@Table(name = PersistenceProperties.AttendanceScheduleSetting.table)
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceScheduleSetting extends SliceJpaObject {

	private static final long serialVersionUID = 4555094494086574586L;
	private static final String TABLE = PersistenceProperties.AttendanceScheduleSetting.table;

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
	@FieldDescribe("顶层组织名称")
	@Column(name = "xtopUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String topUnitName;

	@FieldDescribe("组织名称")
	@Column(name = "xunitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String unitName;

	@FieldDescribe("组织编号")
	@Column(name = "xunitOu", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String unitOu;

	@FieldDescribe("上班时间")
	@Column(name = "xonDutyTime", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String onDutyTime;

	@FieldDescribe("下班时间")
	@Column(name = "xoffDutyTime", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String offDutyTime;

	@FieldDescribe("迟到起算时间")
	@Column(name = "xlateStartTime", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String lateStartTime;

	@FieldDescribe("缺勤起算时间")
	@Column(name = "xabsenceStartTime", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String absenceStartTime;

	@FieldDescribe("早退起算时间")
	@Column(name = "xleaveEarlyStartTime", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String leaveEarlyStartTime;

	public String getUnitName() {
		return unitName;
	}

	public String getUnitOu() {
		return unitOu;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public void setUnitOu(String unitOu) {
		this.unitOu = unitOu;
	}

	public String getOnDutyTime() {
		return onDutyTime;
	}

	public void setOnDutyTime(String onDutyTime) {
		this.onDutyTime = onDutyTime;
	}

	public String getOffDutyTime() {
		return offDutyTime;
	}

	public void setOffDutyTime(String offDutyTime) {
		this.offDutyTime = offDutyTime;
	}

	public String getLateStartTime() {
		return lateStartTime;
	}

	public void setLateStartTime(String lateStartTime) {
		this.lateStartTime = lateStartTime;
	}

	public String getAbsenceStartTime() {
		return absenceStartTime;
	}

	public void setAbsenceStartTime(String absenceStartTime) {
		this.absenceStartTime = absenceStartTime;
	}

	public String getLeaveEarlyStartTime() {
		return leaveEarlyStartTime;
	}

	public void setLeaveEarlyStartTime(String leaveEarlyStartTime) {
		this.leaveEarlyStartTime = leaveEarlyStartTime;
	}

	public String getTopUnitName() {
		return topUnitName;
	}

	public void setTopUnitName(String topUnitName) {
		this.topUnitName = topUnitName;
	}
}