package com.x.program.center.jaxrs.collect;

import com.x.base.core.exception.PromptException;

class PasswordEmptyException extends PromptException {

	private static final long serialVersionUID = -7965962660756955360L;

	PasswordEmptyException() {
		super("密码不能为空.");
	}
}
