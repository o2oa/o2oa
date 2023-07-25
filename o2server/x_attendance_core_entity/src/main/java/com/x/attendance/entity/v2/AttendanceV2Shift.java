package com.x.attendance.entity.v2;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.Strategy;

import javax.persistence.*;

/**
 * 班次
 */
@Schema(name = "AttendanceV2Shift", description = "班次.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = com.x.attendance.entity.PersistenceProperties.AttendanceV2Shift.table, uniqueConstraints = {
		@UniqueConstraint(name = com.x.attendance.entity.PersistenceProperties.AttendanceV2Shift.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2Shift extends SliceJpaObject {


	private static final String TABLE = com.x.attendance.entity.PersistenceProperties.AttendanceV2Shift.table;
	private static final long serialVersionUID = 4442626785709902731L;

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
	public static final String shiftName_FIELDNAME = "shiftName";
	@FieldDescribe("班次名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ shiftName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String shiftName;


	public static final String operator_FIELDNAME = "operator";
	@FieldDescribe("最后操作人")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ operator_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String operator;


	public static final String PROPERTIES_FIELDNAME = "properties";
	@FieldDescribe("班次上下班打卡时间.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + PROPERTIES_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private AttendanceV2ShiftCheckTimeProperties properties;

	public static final String workTime_FIELDNAME = "workTime";
	@FieldDescribe("工作时长分钟数.")
	@Column(name = ColumnNamePrefix + workTime_FIELDNAME)
	private Integer workTime; // 工作时长分钟数，如 480

	public static final String needLimitWorkTime_FIELDNAME = "needLimitWorkTime";
	@FieldDescribe("工作时长不足是否记为早退.")
	@Column(name = ColumnNamePrefix + needLimitWorkTime_FIELDNAME)
	private Boolean needLimitWorkTime = true;

	public static final String seriousTardinessLateTime_FIELDNAME = "seriousTardinessLateMinutes";
	@FieldDescribe("严重迟到分钟数.")
	@Column(name = ColumnNamePrefix + seriousTardinessLateTime_FIELDNAME)
	private Integer seriousTardinessLateMinutes; // 严重迟到分钟数，如 30

	public static final String absenteeismLateTime_FIELDNAME = "absenteeismLateMinutes";
	@FieldDescribe("旷工迟到分钟数.")
	@Column(name = ColumnNamePrefix + absenteeismLateTime_FIELDNAME)
	private Integer absenteeismLateMinutes; // 旷工迟到分钟数，如 30


	public static final String lateAndEarlyOnTime_FIELDNAME = "lateAndEarlyOnTime";
	@FieldDescribe("上班最多可晚时间")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ lateAndEarlyOnTime_FIELDNAME)
	private String lateAndEarlyOnTime; // 上班最多可晚时间 上班晚到几分钟，下班须晚走几分钟。 如 30 表示30分钟

	public static final String lateAndEarlyOffTime_FIELDNAME = "lateAndEarlyOffTime";
	@FieldDescribe("下班最多可早走时间")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ lateAndEarlyOffTime_FIELDNAME)
	private String lateAndEarlyOffTime; // 下班最多可早走时间，上班早到几分钟，下班可早走几分钟 如 90 表示1小时30分钟


	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getShiftName() {
		return shiftName;
	}

	public void setShiftName(String shiftName) {
		this.shiftName = shiftName;
	}

	public AttendanceV2ShiftCheckTimeProperties getProperties() {
		return properties;
	}

	public void setProperties(AttendanceV2ShiftCheckTimeProperties properties) {
		this.properties = properties;
	}

	public Integer getSeriousTardinessLateMinutes() {
		return seriousTardinessLateMinutes;
	}

	public void setSeriousTardinessLateMinutes(Integer seriousTardinessLateMinutes) {
		this.seriousTardinessLateMinutes = seriousTardinessLateMinutes;
	}

	public Integer getAbsenteeismLateMinutes() {
		return absenteeismLateMinutes;
	}

	public void setAbsenteeismLateMinutes(Integer absenteeismLateMinutes) {
		this.absenteeismLateMinutes = absenteeismLateMinutes;
	}

	public String getLateAndEarlyOnTime() {
		return lateAndEarlyOnTime;
	}

	public void setLateAndEarlyOnTime(String lateAndEarlyOnTime) {
		this.lateAndEarlyOnTime = lateAndEarlyOnTime;
	}

	public String getLateAndEarlyOffTime() {
		return lateAndEarlyOffTime;
	}

	public void setLateAndEarlyOffTime(String lateAndEarlyOffTime) {
		this.lateAndEarlyOffTime = lateAndEarlyOffTime;
	}

  public Integer getWorkTime() {
    return workTime == null ? 0 : workTime;
  }

  public void setWorkTime(Integer workTime) {
    this.workTime = workTime;
  }

  public Boolean getNeedLimitWorkTime() {
    return needLimitWorkTime;
  }

  public void setNeedLimitWorkTime(Boolean needLimitWorkTime) {
    this.needLimitWorkTime = needLimitWorkTime;
  }

	

}