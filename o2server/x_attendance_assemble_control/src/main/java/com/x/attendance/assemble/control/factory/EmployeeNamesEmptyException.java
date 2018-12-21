package com.x.attendance.assemble.control.factory;

import com.x.base.core.project.exception.PromptException;

class EmployeeNamesEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	EmployeeNamesEmptyException() {
		super("员工姓名列表不能为空.");
	}
}
