package com.x.attendance.assemble.control.jaxrs.attendanceadmin;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttendanceAdminProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAttendanceAdminProcess(Throwable e, String message ) {
		super("用户在进行考勤管理员信息配置处理时发生异常！message:" + message, e );
	}
}
