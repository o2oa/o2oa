package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.x.base.core.exception.PromptException;

class AttendanceDetailNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceDetailNotExistsException( String id ) {
		super("员工打卡信息不存在！ID:" + id );
	}
}
