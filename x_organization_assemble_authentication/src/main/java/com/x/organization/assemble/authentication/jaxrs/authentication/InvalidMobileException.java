package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.exception.PromptException;

class InvalidMobileException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InvalidMobileException(String mobile) {
		super("用户注册的手机号不正确:" + mobile + ".");
	}
}
