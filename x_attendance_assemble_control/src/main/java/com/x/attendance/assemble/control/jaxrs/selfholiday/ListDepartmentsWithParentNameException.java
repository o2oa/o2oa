package com.x.attendance.assemble.control.jaxrs.selfholiday;

import com.x.base.core.exception.PromptException;

class ListDepartmentsWithParentNameException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ListDepartmentsWithParentNameException( Throwable e, String name ) {
		super("系统根据部门名称查询所有下级部门列表时发生异常.Name:" + name, e );
	}
}
