package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.exception.PromptException;

class CodeEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CodeEmptyException() {
		super("短信验证码不可为空.");
	}
}
