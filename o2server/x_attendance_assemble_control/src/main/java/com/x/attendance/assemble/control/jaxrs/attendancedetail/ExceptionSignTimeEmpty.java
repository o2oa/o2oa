package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.project.exception.PromptException;

class ExceptionSignTimeEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSignTimeEmpty() {
		super("员工手机打卡信息中打卡时间不能为空，格式: HH:mm:ss." );
	}
}
