package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.exception.PromptException;

class IdentityEmptyException extends PromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	IdentityEmptyException() {
		super("identity can not be empty.");
	}
}
