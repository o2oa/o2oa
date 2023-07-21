package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionRetract extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionRetract(String workId) {
		super("撤回失败,工作:{}.", workId);
	}
}
