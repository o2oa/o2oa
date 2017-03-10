package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceDetailMobileIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceDetailMobileIdEmptyException() {
		super("员工手机打卡记录ID为空，无法进行数据查询." );
	}
}
