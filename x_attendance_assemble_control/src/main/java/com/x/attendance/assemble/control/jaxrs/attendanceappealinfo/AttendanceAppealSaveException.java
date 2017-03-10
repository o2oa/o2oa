package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.x.base.core.exception.PromptException;

class AttendanceAppealSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceAppealSaveException( Throwable e ) {
		super("系统在保存申诉信息时发生异常.", e );
	}
}
