package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.x.base.core.exception.PromptException;

class QeuryDepartmentWithPersonException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	QeuryDepartmentWithPersonException( Throwable e, String name ) {
		super("系统根据员工姓名查询部门信息时发生异常！name:" + name, e );
	}
}
