package com.x.program.center.jaxrs.config;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionJsonError extends LanguagePromptException {

	private static final long serialVersionUID = 6084637626229970254L;

	ExceptionJsonError() {
		super("内容为非法json格式.");
	}
}
