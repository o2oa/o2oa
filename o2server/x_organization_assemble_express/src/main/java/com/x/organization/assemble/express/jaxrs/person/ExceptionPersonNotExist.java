package com.x.organization.assemble.express.jaxrs.person;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonNotExist extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionPersonNotExist(String name) {
		super("用户: {} 不存在.", name);
	}
}
