package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidPassword extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionInvalidPassword() {
		super("用户名,密码不正确.");
	}
}
