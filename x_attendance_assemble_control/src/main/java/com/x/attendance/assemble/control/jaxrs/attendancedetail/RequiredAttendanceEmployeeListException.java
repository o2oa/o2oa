package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class RequiredAttendanceEmployeeListException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	RequiredAttendanceEmployeeListException( Throwable e ) {
		super("系统在查询需要考勤的人员配置列表时发生异常.", e );
	}
}
