package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.x.base.core.exception.PromptException;

class AttendanceDetailQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceDetailQueryByIdException( Throwable e, String id ) {
		super("系统在根据ID查询员工打卡信息时发生异常！ID:" + id, e );
	}
}
