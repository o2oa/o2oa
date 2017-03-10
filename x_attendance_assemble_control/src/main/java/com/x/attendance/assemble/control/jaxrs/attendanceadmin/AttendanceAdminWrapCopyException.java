package com.x.attendance.assemble.control.jaxrs.attendanceadmin;

import com.x.base.core.exception.PromptException;

class AttendanceAdminWrapCopyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceAdminWrapCopyException( Throwable e ) {
		super("系统在转换所有管理员信息为输出对象时发生异常.", e );
	}
}
