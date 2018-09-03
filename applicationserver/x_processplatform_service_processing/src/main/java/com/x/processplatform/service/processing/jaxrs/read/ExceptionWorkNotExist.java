package com.x.processplatform.service.processing.jaxrs.read;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkNotExist extends PromptException {

	private static final long serialVersionUID = -2925120598877380881L;

	ExceptionWorkNotExist(String id) {
		super("work: {} not existed.", id);
	}
}