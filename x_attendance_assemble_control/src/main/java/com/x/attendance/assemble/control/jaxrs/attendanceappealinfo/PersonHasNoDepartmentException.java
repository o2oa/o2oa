package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.x.base.core.exception.PromptException;

class PersonHasNoDepartmentException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PersonHasNoDepartmentException( String name ) {
		super( "未能根据员工姓名查询到任何部门信息！name:" + name );
	}
}
