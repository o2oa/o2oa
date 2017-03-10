package com.x.attendance.assemble.control.jaxrs.attendanceadmin;

import com.x.base.core.exception.PromptException;

class AttendanceAdminSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceAdminSaveException( Throwable e ) {
		super("系统保存管理员信息时发生异常.", e );
	}
}
