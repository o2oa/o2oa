package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class GetAttendanceDetailMaxRecordDateException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	GetAttendanceDetailMaxRecordDateException( Throwable e ) {
		super("系统在查询打卡信息记录最大日期时发生异常.", e );
	}
}
