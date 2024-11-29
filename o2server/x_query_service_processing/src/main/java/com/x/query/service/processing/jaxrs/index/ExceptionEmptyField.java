package com.x.query.service.processing.jaxrs.index;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyField extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionEmptyField(String name) {
		super("字段不能为空:{}.", name);
	}

}
