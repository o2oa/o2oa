package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceDetailMobileCountException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;
	
	AttendanceDetailMobileCountException(Exception e, String empNo, String empName, String signDescription, String startDate, String endDate) {
		super("根据条件查询员工手机打卡信息条目数时发生异常.EmpNo:"+ empNo +", EmpName:"+ empName +", SignDescription:"+ signDescription +", StartDate:"+ startDate +", EndDate:" + endDate, e );
	}
}
