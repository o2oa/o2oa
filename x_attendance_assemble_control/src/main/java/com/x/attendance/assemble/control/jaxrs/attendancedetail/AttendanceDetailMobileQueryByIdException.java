package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceDetailMobileQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceDetailMobileQueryByIdException( Throwable e, String id ) {
		super("系统在根据ID查询员工手机打卡信息时发生异常！ID:" + id, e );
	}
}
