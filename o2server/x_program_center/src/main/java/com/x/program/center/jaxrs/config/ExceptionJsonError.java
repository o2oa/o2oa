package com.x.program.center.jaxrs.config;

import com.x.base.core.project.exception.PromptException;

class ExceptionJsonError extends PromptException {

	private static final long serialVersionUID = 6084637626229970254L;

	ExceptionJsonError() {
		super("内容为非法json格式.");
	}
}
