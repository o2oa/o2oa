package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyRouteName extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionEmptyRouteName() {
		super("路由选择不能为空.");
	}
}
