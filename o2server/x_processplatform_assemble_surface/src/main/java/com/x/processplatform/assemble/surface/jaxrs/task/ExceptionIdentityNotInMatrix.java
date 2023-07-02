package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionIdentityNotInMatrix extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionIdentityNotInMatrix(String identity) {
		super("身份:{} 不在待办身份中.");
	}
}
