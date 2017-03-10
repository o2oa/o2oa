package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.x.base.core.exception.PromptException;

class AttendanceAppealNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceAppealNotExistsException( String id ) {
		super("员工打卡申诉信息不存在！ID:" + id );
	}
}
