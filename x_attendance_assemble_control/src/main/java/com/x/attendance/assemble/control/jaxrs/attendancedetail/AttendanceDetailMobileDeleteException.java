package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceDetailMobileDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceDetailMobileDeleteException( Throwable e, String id ) {
		super("系统在保存员工手机打卡信息时发生异常.ID:" + id, e );
	}
}
