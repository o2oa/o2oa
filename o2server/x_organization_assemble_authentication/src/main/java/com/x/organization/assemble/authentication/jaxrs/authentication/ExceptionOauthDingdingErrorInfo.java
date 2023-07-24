package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionOauthDingdingErrorInfo extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionOauthDingdingErrorInfo(String msg) {
		super(msg);
	}
}
