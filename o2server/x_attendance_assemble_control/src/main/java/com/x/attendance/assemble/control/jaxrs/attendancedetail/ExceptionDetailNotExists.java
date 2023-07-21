package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.project.exception.PromptException;

class ExceptionDetailNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionDetailNotExists( String id ) {
		super("员工打卡信息不存在！ID:" + id );
	}
}
