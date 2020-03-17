package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionOauthBindDisable extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionOauthBindDisable() {
		super("绑定已经禁用.");
	}
}
