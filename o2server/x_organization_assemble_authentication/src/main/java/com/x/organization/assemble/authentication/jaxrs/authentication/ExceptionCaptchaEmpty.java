package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionCaptchaEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public static String defaultMessage = "图片验证码不能为空.";

	ExceptionCaptchaEmpty() {
		super(defaultMessage);
	}
}
