package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceDetailRecordDateFormatException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceDetailRecordDateFormatException(Exception e, String recordDateString) {
		super("员工打卡信息中打卡日期格式异常，格式: yyyy-mm-dd. 日期：" + recordDateString );
	}
}
