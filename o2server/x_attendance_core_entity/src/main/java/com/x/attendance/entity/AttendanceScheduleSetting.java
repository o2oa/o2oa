package com.x.attendance.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AttendanceScheduleSetting", description = "考勤定时任务配置.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
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
	public static final String topUnitName_FIELDNAME = "topUnitName";
	@FieldDescribe("顶层组织名称distinguishedName")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + topUnitName_FIELDNAME )
	@Index(name = TABLE + IndexNameMiddle + topUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String topUnitName;

	public static final String unitName_FIELDNAME = "unitName";
	@FieldDescribe("组织名称distinguishedName")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + unitName_FIELDNAME )
	@Index(name = TABLE + IndexNameMiddle + unitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String unitName;

	public static final String unitOu_FIELDNAME = "unitOu";
	@FieldDescribe("组织编号distinguishedName")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + unitOu_FIELDNAME )
	@Index(name = TABLE + IndexNameMiddle + unitOu_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String unitOu;

	public static final String onDutyTime_FIELDNAME = "onDutyTime";
	@FieldDescribe("上午上班时间")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + onDutyTime_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String onDutyTime;

	public static final String middayRestStartTime_FIELDNAME = "middayRestStartTime";
	@FieldDescribe("午休开始时间|上午下班时间")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + middayRestStartTime_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String middayRestStartTime;

	public static final String middayRestEndTime_FIELDNAME = "middayRestEndTime";
	@FieldDescribe("午休结束时间|下午上班时间")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + middayRestEndTime_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String middayRestEndTime;

	public static final String offDutyTime_FIELDNAME = "offDutyTime";
	@FieldDescribe("下午下班时间")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + offDutyTime_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String offDutyTime;

	public static final String signProxy_FIELDNAME = "signProxy";
	@FieldDescribe("打卡策略：1-两次打卡（上午上班，下午下班） 2-三次打卡（上午上班，下午下班加中午一次共三次） 3-四次打卡（上午下午都打上班下班卡）")
	@Column( name = ColumnNamePrefix + signProxy_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private Integer signProxy = 0;

	public static final String lateStartTime_FIELDNAME = "lateStartTime";
	@FieldDescribe("上午上班迟到起算时间")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + lateStartTime_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String lateStartTime;

	public static final String lateStartTimeAfternoon_FIELDNAME = "lateStartTimeAfternoon";
	@FieldDescribe("下午上班迟到起算时间")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + lateStartTimeAfternoon_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String lateStartTimeAfternoon;

	public static final String absenceStartTime_FIELDNAME = "absenceStartTime";
	@FieldDescribe("缺勤起算时间：第一次打卡在什么时间算是当天缺勤")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + absenceStartTime_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String absenceStartTime;

	public static final String leaveEarlyStartTime_FIELDNAME = "leaveEarlyStartTime";
	@FieldDescribe("下午早退起算时间")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + leaveEarlyStartTime_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String leaveEarlyStartTime;

	public static final String leaveEarlyStartTimeMorning_FIELDNAME = "leaveEarlyStartTimeMorning";
	@FieldDescribe("上午早退起算时间")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + leaveEarlyStartTimeMorning_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String leaveEarlyStartTimeMorning;

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

	public String getOnDutyTime() { return onDutyTime; }

	public void setOnDutyTime(String onDutyTime) { this.onDutyTime = onDutyTime; }

	public String getMiddayRestStartTime() { return middayRestStartTime; }

	public void setMiddayRestStartTime(String middayRestStartTime) { this.middayRestStartTime = middayRestStartTime; }

	public String getMiddayRestEndTime() { return middayRestEndTime; }

	public void setMiddayRestEndTime(String middayRestEndTime) { this.middayRestEndTime = middayRestEndTime; }

	public Integer getSignProxy() { return signProxy == null? 1:signProxy; }

	public void setSignProxy(Integer signProxy) { this.signProxy = signProxy; }

	public String getOffDutyTime() { return offDutyTime; }

	public void setOffDutyTime(String offDutyTime) { this.offDutyTime = offDutyTime; }

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

	public String getLateStartTimeAfternoon() { return lateStartTimeAfternoon; }

	public void setLateStartTimeAfternoon(String lateStartTimeAfternoon) { this.lateStartTimeAfternoon = lateStartTimeAfternoon; }

	public String getLeaveEarlyStartTimeMorning() { return leaveEarlyStartTimeMorning; }

	public void setLeaveEarlyStartTimeMorning(String leaveEarlyStartTimeMorning) { this.leaveEarlyStartTimeMorning = leaveEarlyStartTimeMorning; }
}