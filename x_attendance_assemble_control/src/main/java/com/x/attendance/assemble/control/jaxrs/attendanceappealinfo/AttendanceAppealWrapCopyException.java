package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.x.base.core.exception.PromptException;

class AttendanceAppealWrapCopyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceAppealWrapCopyException( Throwable e ) {
		super("系统在转换申诉信息为输出对象时发生异常.", e );
	}
}
