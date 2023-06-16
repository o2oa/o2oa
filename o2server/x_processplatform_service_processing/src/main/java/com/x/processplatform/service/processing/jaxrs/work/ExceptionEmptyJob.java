package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyJob extends PromptException {

	private static final long serialVersionUID = 4681834398891428074L;

	ExceptionEmptyJob() {
		super("job can not be empty.");
	}
}
