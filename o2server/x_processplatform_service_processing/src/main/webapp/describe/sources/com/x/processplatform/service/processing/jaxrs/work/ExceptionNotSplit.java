package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionNotSplit extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionNotSplit(String id) {
		super("work id:{}, not split.", id);
	}
}
