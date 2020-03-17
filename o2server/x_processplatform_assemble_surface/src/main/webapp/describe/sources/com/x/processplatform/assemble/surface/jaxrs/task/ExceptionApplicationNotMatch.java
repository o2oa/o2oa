package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class ExceptionApplicationNotMatch extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionApplicationNotMatch(String application, String requireApplication) {
		super("application: {} not match with: {}.", application, requireApplication);
	}

}
