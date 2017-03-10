package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceDetailMobileSignTimeEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceDetailMobileSignTimeEmptyException() {
		super("员工手机打卡信息中打卡时间不能为空，格式: HH:mm:ss." );
	}
}
