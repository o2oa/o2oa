package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.exception.PromptException;

class PersonNotExistedException extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	PersonNotExistedException(String name) {
		super("用户: {} 不存在.", name);
	}
}
