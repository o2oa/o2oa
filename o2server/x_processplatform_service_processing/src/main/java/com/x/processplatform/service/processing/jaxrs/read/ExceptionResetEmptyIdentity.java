package com.x.processplatform.service.processing.jaxrs.read;

import com.x.base.core.project.exception.PromptException;

class ExceptionResetEmptyIdentity extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionResetEmptyIdentity() {
		super("重置的身份为空.");
	}
}
