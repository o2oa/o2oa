package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.exception.PromptException;

class PersonNotExistedException extends PromptException {

	private static final long serialVersionUID = -6124481323896411121L;

	PersonNotExistedException(String name) {
		super("用户不存在:" + name + ".");
	}
}
