package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.base.core.exception.PromptException;

class GetDepartmentWithNameException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	GetDepartmentWithNameException( Throwable e, String name ) {
		super("系统根据部门名称查询部门信息时发生异常. Name:" + name, e );
	}
}
