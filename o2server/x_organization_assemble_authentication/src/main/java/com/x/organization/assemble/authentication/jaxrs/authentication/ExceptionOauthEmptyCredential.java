package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionOauthEmptyCredential extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	public static String defaultMessage = "无法获取用户凭证.";

	ExceptionOauthEmptyCredential() {
		super(defaultMessage);
	}
}
