package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionPasswordEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionPasswordEmpty() {
		super("密码不能为空.");
	}
}
