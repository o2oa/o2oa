package com.x.organization.assemble.authentication.jaxrs.sso;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyToken extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;
	public static String defaultMessage = "sso 没有提供解码令牌.";

	ExceptionEmptyToken() {
		super(defaultMessage);
	}
}
