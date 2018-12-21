package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.PromptException;

class ExceptionGroupNotExist extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionGroupNotExist(String name) {
		super("群组: {} 不存在.", name);
	}
}
