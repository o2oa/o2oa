package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionOauthEmptyToken extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionOauthEmptyToken() {
		super("请求令牌返回值为空.");
	}
}
