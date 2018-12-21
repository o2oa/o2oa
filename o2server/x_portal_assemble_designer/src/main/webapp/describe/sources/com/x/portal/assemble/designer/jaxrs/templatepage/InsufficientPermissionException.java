package com.x.portal.assemble.designer.jaxrs.templatepage;

import com.x.base.core.project.exception.PromptException;

class InsufficientPermissionException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	InsufficientPermissionException(String person) {
		super("person: {} has insufficient permission.", person);
	}
}
