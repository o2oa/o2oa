package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

class ExceptionJobAccessDenied extends PromptException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionJobAccessDenied(String person, String job) {
		super("person:{} access job:{}, denied.", person, job);
	}

}
