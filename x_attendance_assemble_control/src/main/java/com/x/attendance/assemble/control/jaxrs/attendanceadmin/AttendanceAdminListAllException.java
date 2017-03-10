package com.x.attendance.assemble.control.jaxrs.attendanceadmin;

import com.x.base.core.exception.PromptException;

class AttendanceAdminListAllException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceAdminListAllException() {
		super("系统在获取所有管理员信息时发生异常.");
	}
}
