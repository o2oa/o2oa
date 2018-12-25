package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionProcessNotExist extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionProcessNotExist(String processFlag) {
		super("not found process with:{}.", processFlag);
	}
}
