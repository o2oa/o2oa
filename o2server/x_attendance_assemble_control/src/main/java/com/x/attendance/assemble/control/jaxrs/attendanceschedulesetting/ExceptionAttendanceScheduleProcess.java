package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttendanceScheduleProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAttendanceScheduleProcess( Throwable e, String message ) {
		super("用户在进行考勤时间配置信息处理时发生异常！message:" + message, e );
	}
}
