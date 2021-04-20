package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAlreadyResume extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionAlreadyResume(String id) {
		super("待办已经处于正常状态:{}.", id);
	}
}
