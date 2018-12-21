package com.x.processplatform.service.processing.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyFilter extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionEmptyFilter() {
		super("过滤条件为空.");
	}
}
