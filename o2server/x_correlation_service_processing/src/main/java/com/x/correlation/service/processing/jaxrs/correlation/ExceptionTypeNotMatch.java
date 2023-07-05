package com.x.correlation.service.processing.jaxrs.correlation;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionTypeNotMatch extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionTypeNotMatch(String type, String other) {
		super("类型不匹配:{},{}.", type, other);
	}
}
