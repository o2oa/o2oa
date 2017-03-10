package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.exception.PromptException;

class InvalidPasswordException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InvalidPasswordException() {
		super("用户名,密码不正确.");
	}
}
