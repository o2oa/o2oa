package com.x.organization.assemble.personal.jaxrs.password;

import com.x.base.core.exception.PromptException;

class OldPasswordEmptyException extends PromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	OldPasswordEmptyException() {
		super("无效的操作请求.");
	}

}
