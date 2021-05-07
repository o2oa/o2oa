package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionCodeEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public static String defaultMessage = "短信验证码不可为空.";

	ExceptionCodeEmpty() {
		super(defaultMessage);
	}
}
