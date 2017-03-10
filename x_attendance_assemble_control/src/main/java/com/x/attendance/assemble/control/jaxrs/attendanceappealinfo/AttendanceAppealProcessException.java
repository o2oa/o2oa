package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.x.base.core.exception.PromptException;

class AttendanceAppealProcessException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceAppealProcessException( Throwable e, String id ) {
		super("用户在根据ID处理申诉信息时发生异常！ID:" + id, e );
	}
}
