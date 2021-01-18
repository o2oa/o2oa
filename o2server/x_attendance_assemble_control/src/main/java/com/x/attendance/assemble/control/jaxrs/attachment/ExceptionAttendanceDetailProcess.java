package com.x.attendance.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttendanceDetailProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAttendanceDetailProcess(Throwable e, String message ) {
		super("用户在进行考勤打卡数据信息处理时发生异常！message:" + message, e );
	}
	
	public ExceptionAttendanceDetailProcess(String message ) {
		super("用户在进行考勤打卡数据信息处理时发生异常！message:" + message );
	}
}
