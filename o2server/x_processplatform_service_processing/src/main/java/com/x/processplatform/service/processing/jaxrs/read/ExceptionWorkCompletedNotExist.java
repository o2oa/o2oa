package com.x.processplatform.service.processing.jaxrs.read;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkCompletedNotExist extends PromptException {

	private static final long serialVersionUID = -2925120598877380881L;

	ExceptionWorkCompletedNotExist(String id) {
		super("work: {} not existed.", id);
	}
}