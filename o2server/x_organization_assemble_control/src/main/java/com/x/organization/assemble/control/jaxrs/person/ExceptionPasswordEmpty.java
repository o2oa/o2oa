package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.PromptException;

class ExceptionPasswordEmpty extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionPasswordEmpty() {
		super("密码不能为空.");
	}
}
