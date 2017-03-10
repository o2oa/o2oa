package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.exception.PromptException;

class CredentialEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CredentialEmptyException() {
		super("用户名不能为空.");
	}
}
