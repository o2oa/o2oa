package com.x.organization.assemble.authentication.jaxrs.oauth;

import com.x.base.core.project.exception.PromptException;

class ExceptionScopeNotExist extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionScopeNotExist(String scope) {
		super("scope:{} not exist.", scope);
	}
}
