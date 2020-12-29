package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class ExceptionErrorRouteName extends PromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionErrorRouteName(String routeName) {
		super("路由选择错误:{}.", routeName);
	}
}
