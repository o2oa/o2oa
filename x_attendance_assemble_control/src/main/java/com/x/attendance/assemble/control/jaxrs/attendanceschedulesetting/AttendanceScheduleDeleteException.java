package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.base.core.exception.PromptException;

class AttendanceScheduleDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceScheduleDeleteException( Throwable e, String id ) {
		super("根据ID删除组织排班信息时发生异常.ID:"+id, e );
	}
}
