package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonHasNoIdenitity extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionPersonHasNoIdenitity( String name ) {
		super("员工未设置身份信息，请检查员工所在组织是否正常！name:" + name );
	}
}
