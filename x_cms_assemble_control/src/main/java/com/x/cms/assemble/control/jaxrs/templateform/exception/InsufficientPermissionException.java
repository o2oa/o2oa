package com.x.cms.assemble.control.jaxrs.templateform.exception;

import com.x.base.core.exception.PromptException;

public class InsufficientPermissionException extends PromptException {

	private static final long serialVersionUID = 1148555249431355284L;

	public InsufficientPermissionException(String person) {
		super("insufficient permissions to execute action, person:{}.", person);
	}
}
