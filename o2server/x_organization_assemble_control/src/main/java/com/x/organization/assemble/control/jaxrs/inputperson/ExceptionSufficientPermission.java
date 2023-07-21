package com.x.organization.assemble.control.jaxrs.inputperson;

import com.x.base.core.project.exception.PromptException;

class ExceptionSufficientPermission extends PromptException {

	private static final long serialVersionUID = 7237855733312562652L;

	ExceptionSufficientPermission(String name) {
		super("person: {} has sufficient permission.", name);

	}
}
