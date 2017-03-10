package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceDetailArchiveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceDetailArchiveException( Throwable e, String id ) {
		super("系统归档员工打卡信息时发生异常！ID:" + id, e );
	}
}
