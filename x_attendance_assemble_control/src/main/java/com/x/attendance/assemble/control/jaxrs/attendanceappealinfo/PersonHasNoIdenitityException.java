package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.x.base.core.exception.PromptException;

class PersonHasNoIdenitityException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PersonHasNoIdenitityException( String name ) {
		super("员工未设置身份信息，请检查员工所在部门是否正常！name:" + name );
	}
}
