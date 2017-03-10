package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceDetailOffDutyTimeFormatException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceDetailOffDutyTimeFormatException(Exception e, String recordDateString) {
		super("员工下班打卡时间格式异常，格式: HH:mm:ss. 日期：" + recordDateString );
	}
}
