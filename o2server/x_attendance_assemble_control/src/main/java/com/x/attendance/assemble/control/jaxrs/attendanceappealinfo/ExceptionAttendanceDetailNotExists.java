package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttendanceDetailNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAttendanceDetailNotExists( String id ) {
		super("员工打卡信息不存在！ID:" + id );
	}
}
