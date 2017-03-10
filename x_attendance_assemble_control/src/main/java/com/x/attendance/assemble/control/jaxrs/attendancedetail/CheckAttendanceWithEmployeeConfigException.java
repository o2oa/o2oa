package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class CheckAttendanceWithEmployeeConfigException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CheckAttendanceWithEmployeeConfigException( Throwable e ) {
		super("系统检查需要考勤员工配置信息时发生异常.", e );
	}
}
