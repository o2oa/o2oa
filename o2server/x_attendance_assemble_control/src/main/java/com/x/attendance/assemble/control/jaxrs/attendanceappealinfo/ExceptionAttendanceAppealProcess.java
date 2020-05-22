package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttendanceAppealProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAttendanceAppealProcess( Throwable e, String message ) {
		super("用户在进行考勤结果申诉信息处理时发生异常！message:" + message, e );
	}
	
	public ExceptionAttendanceAppealProcess( String message ) {
		super( message );
	}
}
