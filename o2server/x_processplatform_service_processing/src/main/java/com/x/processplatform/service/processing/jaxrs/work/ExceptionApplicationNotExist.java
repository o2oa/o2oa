package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionApplicationNotExist extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionApplicationNotExist(String applicationFlag) {
		super("not found application with:{}.", applicationFlag);
	}
}
