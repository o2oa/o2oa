package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionCodeEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCodeEmpty() {
		super("短信验证码不可为空.");
	}
}
