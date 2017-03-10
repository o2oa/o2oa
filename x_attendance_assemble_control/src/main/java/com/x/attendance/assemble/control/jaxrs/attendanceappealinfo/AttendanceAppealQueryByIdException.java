package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.x.base.core.exception.PromptException;

class AttendanceAppealQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceAppealQueryByIdException( Throwable e, String id ) {
		super("系统在根据ID查询申诉信息时发生异常！ID:" + id, e );
	}
}
