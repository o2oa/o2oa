package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceDetailMobileRecordDateFormatException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceDetailMobileRecordDateFormatException(Exception e, String recordDateString) {
		super("员工手机打卡信息中打卡日期格式异常，格式: yyyy-mm-dd. 日期：" + recordDateString );
	}
}
