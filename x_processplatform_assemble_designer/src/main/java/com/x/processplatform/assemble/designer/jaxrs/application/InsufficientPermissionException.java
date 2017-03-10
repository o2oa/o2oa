package com.x.processplatform.assemble.designer.jaxrs.application;

import com.x.base.core.exception.PromptException;

public class InsufficientPermissionException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	InsufficientPermissionException(String person) {
		super("person: {} has insufficient permission.", person);
	}
}
