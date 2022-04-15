package com.x.processplatform.service.processing.jaxrs.event;

import com.x.base.core.project.exception.PromptException;

class ExceptionTargetEmpty extends PromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionTargetEmpty() {
		super("target不能为空.");
	}
}
