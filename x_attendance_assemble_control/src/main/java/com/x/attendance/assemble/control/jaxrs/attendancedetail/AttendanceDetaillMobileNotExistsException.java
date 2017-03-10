package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceDetaillMobileNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceDetaillMobileNotExistsException( String id ) {
		super("员工手机打卡信息不存在！ID:" + id );
	}
}
