package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionOauthEmptyCredential extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionOauthEmptyCredential() {
		super("无法获取用户凭证.");
	}
}
