package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonHasNoUnit extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionPersonHasNoUnit( String name ) {
		super( "未能根据员工姓名查询到任何组织信息！name:" + name );
	}
}
