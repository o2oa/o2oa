package com.x.organization.assemble.personal.jaxrs.password;

import com.x.base.core.exception.PromptException;

class OldPasswordNotMatchException extends PromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	OldPasswordNotMatchException() {
		super("原密码错误.");
	}

}
