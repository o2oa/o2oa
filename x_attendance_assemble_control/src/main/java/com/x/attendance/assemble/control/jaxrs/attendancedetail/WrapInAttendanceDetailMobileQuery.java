package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;

@Wrap( AttendanceDetailMobile.class)
public class WrapInAttendanceDetailMobileQuery {

	@EntityFieldDescribe( "员工号，根据员工号查询记录" )
	private String empNo;

	@EntityFieldDescribe( "员工姓名，根据员工姓名查询记录." )
	private String empName;

	@EntityFieldDescribe( "开始日期：yyyy-mm-dd." )
	private String startDate;
	
	@EntityFieldDescribe( "结束日期：yyyy-mm-dd,如果开始日期填写，结束日期不填写就是只查询开始日期那一天" )
	private String endDate;

	@EntityFieldDescribe( "打卡说明:上班打卡，下班打卡." )
	private String signDescription;

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

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getSignDescription() {
		return signDescription;
	}

	public void setSignDescription(String signDescription) {
		this.signDescription = signDescription;
	}

}