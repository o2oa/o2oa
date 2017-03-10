package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.base.core.exception.PromptException;

class GetAttendanceScheduleByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	GetAttendanceScheduleByIdException( Throwable e, String id ) {
		super("系统根据ID查询指定组织排班信息时发生异常.ID:" + id, e );
	}
}
