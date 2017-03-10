package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceDetailMobileSignTimeFormatException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceDetailMobileSignTimeFormatException(Exception e, String signTime) {
		super("员工手机打卡信息中打卡时间格式异常，格式:  HH:mm:ss. 时间：" + signTime );
	}
}
