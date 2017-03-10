package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceDetailRecordDateEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceDetailRecordDateEmptyException() {
		super("员工打卡信息中打卡日期不能为空，格式: yyyy-mm-dd." );
	}
}
