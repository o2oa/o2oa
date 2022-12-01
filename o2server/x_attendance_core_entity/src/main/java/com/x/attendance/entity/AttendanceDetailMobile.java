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

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AttendanceDetailMobile", description = "移动端考勤信息.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceDetailMobile.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceDetailMobile.table
		+ JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
				JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceDetailMobile extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.AttendanceDetailMobile.table;

	public AttendanceDetailMobile() {
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
		this.setSequence(StringUtils.join(this.empName, this.recordDateString, this.getId()));
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
	public static final String empNo_FIELDNAME = "empNo";
	@FieldDescribe("员工号")
	@Column( length = JpaObject.length_96B, name = ColumnNamePrefix + empNo_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String empNo;

	public static final String empName_FIELDNAME = "empName";
	@FieldDescribe("员工姓名distinguishedName")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + empName_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String empName;

	public static final String recordDateString_FIELDNAME = "recordDateString";
	@FieldDescribe("打卡记录日期字符串")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + recordDateString_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String recordDateString;

	public static final String recordDate_FIELDNAME = "recordDate";
	@FieldDescribe("打卡记录日期")
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = ColumnNamePrefix + recordDate_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private Date recordDate;

	public static final String CHECKIN_TYPE_ONDUTY = "上午上班打卡";
	public static final String CHECKIN_TYPE_MORNING_OFFDUTY = "上午下班打卡";
	public static final String CHECKIN_TYPE_AFTERNOON_ONDUTY = "下午上班打卡";
	public static final String CHECKIN_TYPE_OFFDUTY = "下午下班打卡";
	public static final String CHECKIN_TYPE_AFTERNOON = "午间打卡";
	//	public static final String CHECKIN_TYPE_OUTSIDE = "外出打卡";

	public static final String checkin_type_FIELDNAME = "checkin_type";
	@FieldDescribe("打卡类型。字符串，目前有：上午上班打卡，上午下班打卡，下午上班打卡，下午下班打卡，外出打卡，午间打卡")
	@Column(name = ColumnNamePrefix + checkin_type_FIELDNAME, length = length_32B)
	private String checkin_type;

	public static final String checkin_time_FIELDNAME = "checkin_time";
	@FieldDescribe("打卡时间。Unix时间戳")
	@Column(name = ColumnNamePrefix + checkin_time_FIELDNAME )
	private long checkin_time;

	public static final String signTime_FIELDNAME = "signTime";
	@FieldDescribe("打卡时间")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + signTime_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String signTime;

	public static final String signDescription_FIELDNAME = "signDescription";
	@FieldDescribe("打卡说明")
	@Column( length = JpaObject.length_128B, name = ColumnNamePrefix + signDescription_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String signDescription;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("其他说明备注")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String recordAddress_FIELDNAME = "recordAddress";
	@FieldDescribe("打卡地点描述")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + recordAddress_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String recordAddress;

	public static final String longitude_FIELDNAME = "longitude";
	@FieldDescribe("经度")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + longitude_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String longitude;

	public static final String latitude_FIELDNAME = "latitude";
	@FieldDescribe("纬度")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + latitude_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String latitude;

	public static final String optMachineType_FIELDNAME = "optMachineType";
	@FieldDescribe("操作设备类别：手机品牌|PAD|PC|其他")
	@Column( length = JpaObject.length_128B, name = ColumnNamePrefix + optMachineType_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String optMachineType;

	public static final String optSystemName_FIELDNAME = "optSystemName";
	@FieldDescribe("操作设备类别：Mac|Windows|IOS|Android|其他")
	@Column( length = JpaObject.length_128B, name = ColumnNamePrefix + optSystemName_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String optSystemName;

	public static final String recordStatus_FIELDNAME = "recordStatus";
	@FieldDescribe("记录状态：0-未分析 1-已分析")
	@Column( name = ColumnNamePrefix + recordStatus_FIELDNAME )
	private Integer recordStatus = 0;

	public static final String workAddress_FIELDNAME = "workAddress";
	@FieldDescribe("打卡地点描述")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + workAddress_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String workAddress;

	public static final String isExternal_FIELDNAME = "isExternal";
	@FieldDescribe("是否范围外打卡")
	@Column(name = ColumnNamePrefix + isExternal_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean isExternal = false;

	public String getEmpNo() {
		return empNo;
	}

	public void setEmpNo(String empNo) {
		this.empNo = empNo;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getRecordDateString() {
		return recordDateString;
	}

	public void setRecordDateString(String recordDateString) {
		this.recordDateString = recordDateString;
	}

	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}

	public String getSignTime() {
		return signTime;
	}

	public void setSignTime(String signTime) {
		this.signTime = signTime;
	}

	public String getSignDescription() {
		return signDescription;
	}

	public void setSignDescription(String signDescription) {
		this.signDescription = signDescription;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRecordAddress() {
		return recordAddress;
	}

	public void setRecordAddress(String recordAddress) {
		this.recordAddress = recordAddress;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getOptMachineType() {
		return optMachineType;
	}

	public void setOptMachineType(String optMachineType) {
		this.optMachineType = optMachineType;
	}

	public String getOptSystemName() {
		return optSystemName;
	}

	public void setOptSystemName(String optSystemName) {
		this.optSystemName = optSystemName;
	}

	public Integer getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(Integer recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getCheckin_type() { return checkin_type; }

	public void setCheckin_type(String checkin_type) { this.checkin_type = checkin_type; }

	public long getCheckin_time() { return checkin_time; }

	public void setCheckin_time(long checkin_time) { this.checkin_time = checkin_time; }

	public String getWorkAddress() {
		return workAddress;
	}

	public void setWorkAddress(String workAddress) {
		this.workAddress = workAddress;
	}

	public Boolean getIsExternal() {
		return isExternal;
	}

	public void setIsExternal(Boolean isExternal) {
		this.isExternal = isExternal;
	}
}