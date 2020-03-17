package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class ExceptionIdentityEmpty extends PromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionIdentityEmpty() {
		super("身份不能为空.");
	}
}
