package com.x.organization.assemble.control.jaxrs.identity;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonBanned extends PromptException {

	private static final long serialVersionUID = 1638670949491210510L;

	ExceptionPersonBanned(String name) {
		super("用户:{}已被禁用.", name);
	}
}
