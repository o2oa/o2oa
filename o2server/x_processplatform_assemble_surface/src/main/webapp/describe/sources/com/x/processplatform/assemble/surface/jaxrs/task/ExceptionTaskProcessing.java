package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class ExceptionTaskProcessing extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionTaskProcessing(String id) {
		super("task {} processing error.", id);
	}

}
