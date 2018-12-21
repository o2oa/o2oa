package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.PromptException;

class ExceptionDenyDeleteInitialManager extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyDeleteInitialManager() {
		super("不能删除初始管理员.");
	}
}
