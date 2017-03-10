package com.x.processplatform.assemble.designer.jaxrs.templateform;

import com.x.base.core.exception.PromptException;

class InsufficientPermissionException extends PromptException {

	private static final long serialVersionUID = 1148555249431355284L;

	InsufficientPermissionException(String person) {
		super("insufficient permissions to execute action, person:{}.", person);
	}
}
