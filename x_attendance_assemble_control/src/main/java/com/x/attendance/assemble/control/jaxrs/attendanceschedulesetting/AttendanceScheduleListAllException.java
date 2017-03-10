package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.base.core.exception.PromptException;

class AttendanceScheduleListAllException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceScheduleListAllException( Throwable e ) {
		super("系统查询所有组织排班信息列表时发生异常.", e );
	}
}
