package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceDetailMobileWrapCopyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceDetailMobileWrapCopyException( Throwable e ) {
		super("系统在转换员工手机打卡信息为输出对象时发生异常.", e );
	}
}
