package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNotFoundTaskOrTaskCompleted extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionNotFoundTaskOrTaskCompleted(String flag) {
		super("无法找到待办或者已办,标识:{}.", flag);
	}
}
