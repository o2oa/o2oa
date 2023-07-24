package com.x.meeting.assemble.control.jaxrs.config;

import com.x.base.core.project.exception.PromptException;

class ExceptionCustomError extends PromptException {

	private static final long serialVersionUID = 5132563473206717992L;

	ExceptionCustomError(String msg) {
		super(msg);
	}
}
