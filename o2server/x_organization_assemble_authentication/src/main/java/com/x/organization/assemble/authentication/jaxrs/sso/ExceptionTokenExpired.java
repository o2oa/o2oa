package com.x.organization.assemble.authentication.jaxrs.sso;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionTokenExpired extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;
	public static String defaultMessage = "token超时.";

	ExceptionTokenExpired() {
		super(defaultMessage);
	}
}
