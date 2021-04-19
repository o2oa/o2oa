package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionOauthEmptyAccessToken extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	public static String defaultMessage = "无法获取访问令牌.";

	ExceptionOauthEmptyAccessToken() {
		super(defaultMessage);
	}
}
