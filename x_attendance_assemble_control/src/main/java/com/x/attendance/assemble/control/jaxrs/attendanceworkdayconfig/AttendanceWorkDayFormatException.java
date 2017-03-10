package com.x.attendance.assemble.control.jaxrs.attendanceworkdayconfig;

import com.x.base.core.exception.PromptException;

class AttendanceWorkDayFormatException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttendanceWorkDayFormatException(Exception e, String date ) {
		super("系统在格式化节假日配置的日期时发生异常.Date:" + date, e );
	}
}
