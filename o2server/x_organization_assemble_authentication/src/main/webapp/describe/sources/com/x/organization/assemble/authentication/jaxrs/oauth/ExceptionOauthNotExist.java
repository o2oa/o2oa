package com.x.organization.assemble.authentication.jaxrs.oauth;

import com.x.base.core.project.exception.PromptException;

class ExceptionOauthNotExist extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionOauthNotExist(String clientId) {
		super("oauth not exist with client_id:{}.", clientId);
	}
}
