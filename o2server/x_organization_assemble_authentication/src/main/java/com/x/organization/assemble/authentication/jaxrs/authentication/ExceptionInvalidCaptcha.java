package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidCaptcha extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public static String defaultMessage = "图片验证码错误.";

	ExceptionInvalidCaptcha() {
		super(defaultMessage);
	}
}
