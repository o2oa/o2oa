package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionIdentityNotExist extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionIdentityNotExist(String identity) {
		super("not found identity with:{}.", identity);
	}
}
