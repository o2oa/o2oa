package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonNotExistOrInvalidPassword extends PromptException {

	private static final long serialVersionUID = -6124481323896411121L;

	ExceptionPersonNotExistOrInvalidPassword( ) {
		super("用户不存在或者密码错误.");
	}
}
