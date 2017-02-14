package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;

@Wrap( AttendanceDetailMobile.class)
public class WrapInAttendanceDetailMobile {
	
	@EntityFieldDescribe( "Id, 可以为空." )
	private String id;
	
	@EntityFieldDescribe( "员工号, 可以为空." )
	private String empNo;

	@EntityFieldDescribe( "员工姓名, 必须填写." )
	private String empName;

	@EntityFieldDescribe( "打卡记录日期字符串：yyyy-mm-dd, 必须填写." )
	private String recordDateString;	

	@EntityFieldDescribe( "打卡时间: hh24:mi:ss, 必须填写." )
	private String signTime;

	@EntityFieldDescribe( "打卡说明:上班打卡，下班打卡, 可以为空." )
	private String signDescription;

	@EntityFieldDescribe( "其他说明备注, 可以为空." )
	private String description;

	@EntityFieldDescribe( "打卡地点描述, 可以为空." )
	private String recordAddress = "未知";
	
	@EntityFieldDescribe( "经度, 可以为空." )
	private String longitude;

	@EntityFieldDescribe( "纬度, 可以为空." )
	private String latitude;

	@EntityFieldDescribe( "操作设备类别：手机品牌|PAD|PC|其他, 可以为空." )
	private String optMachineType = "其他";

	@EntityFieldDescribe( "操作设备类别：Mac|Windows|IOS|Android|其他, 可以为空." )
	private String optSystemName = "其他";

	public String getRecordDateString() {
		return recordDateString;
	}
	public void setRecordDateString(String recordDateString) {
		this.recordDateString = recordDateString;
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
	public String getSignTime() {
		return signTime;
	}
	public void setSignTime(String signTime) {
		this.signTime = signTime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}