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

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@ContainerEntity
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
	@FieldDescribe("员工号")
	@Column(name = "xempNo", length = JpaObject.length_96B)
	@CheckPersist(allowEmpty = true)
	private String empNo;

	@FieldDescribe("员工姓名")
	@Column(name = "xempName", length = JpaObject.length_96B)
	@CheckPersist(allowEmpty = true)
	private String empName;

	@FieldDescribe("打卡记录日期字符串")
	@Column(name = "xrecordDateString", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String recordDateString;

	@FieldDescribe("打卡记录日期")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "xrecordDate")
	@CheckPersist(allowEmpty = true)
	private Date recordDate;

	@FieldDescribe("打卡时间")
	@Column(name = "xsignTime", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String signTime;

	@FieldDescribe("打卡说明")
	@Column(name = "xsignDescription", length = JpaObject.length_128B)
	@CheckPersist(allowEmpty = true)
	private String signDescription;

	@FieldDescribe("其他说明备注")
	@Column(name = "xdescription", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String description;

	@FieldDescribe("打卡地点描述")
	@Column(name = "xrecordAddress", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String recordAddress;

	@FieldDescribe("经度")
	@Column(name = "xlongitude", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String longitude;

	@FieldDescribe("纬度")
	@Column(name = "xlatitude", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String latitude;

	@FieldDescribe("操作设备类别：手机品牌|PAD|PC|其他")
	@Column(name = "xoptMachineType", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String optMachineType;

	@FieldDescribe("操作设备类别：Mac|Windows|IOS|Android|其他")
	@Column(name = "xoptSystemName", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String optSystemName;

	@FieldDescribe("记录状态：0-未分析 1-已分析")
	@Column(name = "xrecordStatus")
	private Integer recordStatus = 0;

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

}