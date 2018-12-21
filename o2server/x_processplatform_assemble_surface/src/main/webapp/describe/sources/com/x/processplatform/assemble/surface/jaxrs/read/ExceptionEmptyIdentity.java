package com.x.processplatform.assemble.surface.jaxrs.read;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyIdentity extends PromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionEmptyIdentity() {
		super("identity can not be empty.");
	}
}
