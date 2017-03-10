package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceDetailSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceDetailSaveException( Throwable e ) {
		super("系统在保存员工打卡信息时发生异常。", e );
	}
}
