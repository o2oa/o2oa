package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.x.base.core.exception.PromptException;

class NotifyAttendanceAppealException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	NotifyAttendanceAppealException( Throwable e, String name ) {
		super("申诉信息提交成功，向申诉当前处理人发送通知消息发生异常！name:" + name, e );
	}
}
