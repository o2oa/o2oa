package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkProcessing extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionWorkProcessing(String id) {
		super("work {} processing error.", id);
	}

}
