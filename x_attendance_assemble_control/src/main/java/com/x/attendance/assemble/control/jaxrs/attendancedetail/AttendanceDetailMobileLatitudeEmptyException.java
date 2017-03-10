package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceDetailMobileLatitudeEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceDetailMobileLatitudeEmptyException() {
		super("员工手机打卡信息中打卡地址纬度信息不能为空." );
	}
}
