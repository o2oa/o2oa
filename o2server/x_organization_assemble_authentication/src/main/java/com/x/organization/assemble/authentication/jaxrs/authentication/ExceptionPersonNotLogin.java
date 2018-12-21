package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonNotLogin extends PromptException {

	private static final long serialVersionUID = -6124481323896411121L;

	ExceptionPersonNotLogin() {
		super("用户未登录.");
	}
}
