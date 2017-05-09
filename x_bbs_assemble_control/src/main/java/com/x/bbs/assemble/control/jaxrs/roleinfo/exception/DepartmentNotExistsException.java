package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.exception.PromptException;

public class DepartmentNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public DepartmentNotExistsException( String department ) {
		super("部门信息不存在！Department:" + department );
	}
}
