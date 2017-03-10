package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.exception.PromptException;

class NoneIdentityException extends PromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	NoneIdentityException(String person) {
		super("can not get identity of person: {}.", person);
	}
}
