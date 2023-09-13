package com.x.attendance.entity.v2;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AttendanceV2GroupSchedule", description = "考勤组排班数据.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceV2GroupSchedule.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceV2GroupSchedule.table
        + JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2GroupSchedule  extends SliceJpaObject {


	private static final long serialVersionUID = 3969460398955957421L;
	private static final String TABLE = com.x.attendance.entity.PersistenceProperties.AttendanceV2GroupSchedule.table;

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

	public static final String groupId_FIELDNAME = "groupId";
	@FieldDescribe("考勤组id.")
	@Column(length = length_64B, name = ColumnNamePrefix + groupId_FIELDNAME)
  @CheckPersist(allowEmpty = false)
	private String groupId;

  public static final String userId_FIELDNAME = "userId";
  @FieldDescribe("排班用户标识DN")
  @Column(length = length_96B, name = ColumnNamePrefix + userId_FIELDNAME)
  @CheckPersist(allowEmpty = false)
  private String userId;

	public static final String scheduleMonthString_FIELDNAME = "scheduleMonthString";
  @FieldDescribe("排班月份: YYYY-MM")
  @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + scheduleMonthString_FIELDNAME)
  @CheckPersist(allowEmpty = false)
  private String scheduleMonthString;

  public static final String scheduleDateString_FIELDNAME = "scheduleDateString";
  @FieldDescribe("排班日期: YYYY-MM-dd")
  @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + scheduleDateString_FIELDNAME)
  @CheckPersist(allowEmpty = false)
  private String scheduleDateString;
   
  public static final String shiftId_FIELDNAME = "shiftId";
	@FieldDescribe("当天绑定的班次id, 为空就是休息.")
	@Column(length = length_64B, name = ColumnNamePrefix + shiftId_FIELDNAME)
	private String shiftId;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	 

	public String getShiftId() {
		return shiftId;
	}

	public void setShiftId(String shiftId) {
		this.shiftId = shiftId;
	}

	public String getScheduleMonthString() {
		return scheduleMonthString;
	}

	public void setScheduleMonthString(String scheduleMonthString) {
		this.scheduleMonthString = scheduleMonthString;
	}

	public String getScheduleDateString() {
		return scheduleDateString;
	}

	public void setScheduleDateString(String scheduleDateString) {
		this.scheduleDateString = scheduleDateString;
	}

	


  

}
