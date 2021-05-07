package com.x.processplatform.assemble.surface.jaxrs.read;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyIdentity extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionEmptyIdentity() {
		super("身份不能为空.");
	}
}
