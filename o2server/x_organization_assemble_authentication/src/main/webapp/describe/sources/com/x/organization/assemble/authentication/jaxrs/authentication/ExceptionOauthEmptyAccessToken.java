package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionOauthEmptyAccessToken extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionOauthEmptyAccessToken() {
		super("无法获取访问令牌.");
	}
}
