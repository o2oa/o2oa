package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionCredentialEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCredentialEmpty() {
		super("用户名不能为空.");
	}
}
