package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.base.core.exception.PromptException;

class AttendanceScheduleSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceScheduleSaveException( Throwable e ) {
		super("保存组织排班信息时发生异常.", e );
	}
}
