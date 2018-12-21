package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidCodeAnswer extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionInvalidCodeAnswer() {
		super("短信验证码错误.");
	}
}
