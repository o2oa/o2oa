package com.x.meeting.assemble.control.jaxrs.meeting;

import com.x.base.core.project.exception.PromptException;

class ExceptionCustomError extends PromptException {

	private static final long serialVersionUID = 6472220739566758708L;

	ExceptionCustomError(String msg) {
		super(msg);
	}
}
