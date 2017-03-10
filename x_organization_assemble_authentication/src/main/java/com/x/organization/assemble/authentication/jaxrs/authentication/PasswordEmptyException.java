package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.exception.PromptException;

class PasswordEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PasswordEmptyException() {
		super("密码不能为空.");
	}
}
