package com.x.attendance.assemble.control.factory;

import com.x.base.core.exception.PromptException;

class DepartmentNamesEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	DepartmentNamesEmptyException() {
		super("部门名称列表不能为空.");
	}
}
