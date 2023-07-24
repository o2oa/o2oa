package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidCodeAnswer extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionInvalidCodeAnswer() {
		super("短信验证码错误.");
	}
}
