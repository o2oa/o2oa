package com.x.attendance.assemble.control.jaxrs.attendanceemployeeconfig;

import com.x.base.core.project.exception.PromptException;

class ExceptionConfigNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionConfigNotExists( String id ) {
		super("指定的人员考勤配置数据不存在.ID:" + id );
	}
}
