package com.x.program.center.jaxrs.collect;

import com.x.base.core.exception.PromptException;

class InvalidCodeAnswerException extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	InvalidCodeAnswerException() {
		super("短信验证码错误.");
	}
}
