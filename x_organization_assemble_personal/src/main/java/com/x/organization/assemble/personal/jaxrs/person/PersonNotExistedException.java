package com.x.organization.assemble.personal.jaxrs.person;

import com.x.base.core.exception.PromptException;

class PersonNotExistedException extends PromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	PersonNotExistedException(String name) {
		super("指定的用户:" + name + ", 不存在.");
	}

}
