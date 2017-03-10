package com.x.organization.assemble.personal.jaxrs.password;

import com.x.base.core.exception.PromptException;

class NewPasswordEmptyException extends PromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	NewPasswordEmptyException() {
		super("新密码不能为空.");
	}

}
