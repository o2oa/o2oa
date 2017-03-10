package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class DepartmentNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	DepartmentNotExistsException( String department ) {
		super("部门信息不存在！Department:" + department );
	}
}
