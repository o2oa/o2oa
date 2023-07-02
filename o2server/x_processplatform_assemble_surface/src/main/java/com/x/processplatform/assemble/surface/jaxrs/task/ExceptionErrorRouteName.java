package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionErrorRouteName extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionErrorRouteName(String routeName) {
		super("路由选择错误:{}.", routeName);
	}
}
