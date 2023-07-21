package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.base.core.project.exception.PromptException;

class ExceptionScheduleNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionScheduleNotExists( String id ) {
		super("指定的组织排班信息不存在.ID:" + id );
	}
}
