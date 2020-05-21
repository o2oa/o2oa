package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.project.exception.PromptException;

class ExceptionLatitudeEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionLatitudeEmpty() {
		super("员工手机打卡信息中打卡地址纬度信息不能为空." );
	}
}
