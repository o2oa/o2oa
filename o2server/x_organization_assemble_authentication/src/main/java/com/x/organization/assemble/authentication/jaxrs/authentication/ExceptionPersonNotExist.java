package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonNotExist extends PromptException {

	private static final long serialVersionUID = -6124481323896411121L;

	ExceptionPersonNotExist(String name) {
		super("用户 {} 不存在.");
	}
}
