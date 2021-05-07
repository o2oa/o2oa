package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionOauthBindDisable extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	public static String defaultMessage = "绑定已经禁用.";

	ExceptionOauthBindDisable() {
		super(defaultMessage);
	}
}
