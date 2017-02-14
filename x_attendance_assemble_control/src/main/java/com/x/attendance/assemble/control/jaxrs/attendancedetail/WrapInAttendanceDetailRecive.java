package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.http.annotation.Wrap;

/**
 * 1-员工姓名 EmployeeName	
   2-员工号   EmployeeNo
   3-日期	RecordDateString
   4-签到时间  OnDutyTime
   5-签退时间  OffDutyTime
 * @author liyi_
 *
 */
@Wrap( AttendanceDetail.class)
public class WrapInAttendanceDetailRecive {

	private String empName = null;
	private String empNo = null;
	private String recordDateString = null;
	private String onDutyTime = null;
	private String offDutyTime = null;
	
	
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public String getEmpNo() {
		return empNo;
	}
	public void setEmpNo(String empNo) {
		this.empNo = empNo;
	}
	public String getRecordDateString() {
		return recordDateString;
	}
	public void setRecordDateString(String recordDateString) {
		this.recordDateString = recordDateString;
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
}