package com.x.processplatform.service.processing.jaxrs.read;

import com.x.base.core.project.exception.PromptException;

class ExceptionReadNotExist extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionReadNotExist(String id) {
		super("read id :{} not exist.", id);
	}
}
