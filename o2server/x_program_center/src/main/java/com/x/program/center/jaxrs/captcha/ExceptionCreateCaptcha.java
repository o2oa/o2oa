package com.x.program.center.jaxrs.captcha;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionCreateCaptcha extends LanguagePromptException {

	private static final long serialVersionUID = -326121174563758109L;

	ExceptionCreateCaptcha() {
		super("create captcha error.");
	}
}
