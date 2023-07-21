package com.x.processplatform.service.processing.jaxrs.read;

import com.x.base.core.project.exception.PromptException;

class ExceptionResetEmpty extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionResetEmpty() {
		super("重置处理人为空.");
	}
}
