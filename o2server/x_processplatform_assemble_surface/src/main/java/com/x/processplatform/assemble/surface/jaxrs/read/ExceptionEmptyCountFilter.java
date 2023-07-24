package com.x.processplatform.assemble.surface.jaxrs.read;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyCountFilter extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionEmptyCountFilter() {
		super("待办数量过滤条件为空.");
	}
}
