package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class ListDepartmentNameByParentNameException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ListDepartmentNameByParentNameException( Throwable e, String departName ) {
		super("根据部门名称列示所有下级部门名称发生异常！Department:" + departName, e );
	}
}
