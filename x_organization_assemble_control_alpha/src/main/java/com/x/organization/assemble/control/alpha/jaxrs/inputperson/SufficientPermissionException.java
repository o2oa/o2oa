package com.x.organization.assemble.control.alpha.jaxrs.inputperson;

import com.x.base.core.exception.PromptException;

class SufficientPermissionException extends PromptException {

	private static final long serialVersionUID = 7237855733312562652L;

	SufficientPermissionException(String name) {
		super("person: {} has sufficient permission.", name);

	}
}
