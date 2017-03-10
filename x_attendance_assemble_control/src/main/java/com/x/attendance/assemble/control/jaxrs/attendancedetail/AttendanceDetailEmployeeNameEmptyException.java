package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceDetailEmployeeNameEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceDetailEmployeeNameEmptyException() {
		super("员打卡信息中员工姓名不能为空." );
	}
}
