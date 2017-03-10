package com.x.program.center.jaxrs.collect;

import com.x.base.core.exception.PromptException;

class CodeAnswerEmptyException extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	CodeAnswerEmptyException() {
		super("短信验证码不可为空.");
	}
}
