package com.x.cms.assemble.control.jaxrs.templateform;

import com.x.base.core.project.exception.PromptException;

class ExceptionInsufficientPermission extends PromptException {

	private static final long serialVersionUID = 1148555249431355284L;

	ExceptionInsufficientPermission(String person) {
		super("insufficient permissions to execute action, person:{}.", person);
	}
}
