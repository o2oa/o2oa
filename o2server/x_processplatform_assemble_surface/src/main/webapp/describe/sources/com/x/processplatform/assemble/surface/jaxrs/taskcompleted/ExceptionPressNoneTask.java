package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import com.x.base.core.project.exception.PromptException;

class ExceptionPressNoneTask extends PromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionPressNoneTask(String id) {
		super("工作: {} 目前没有待办.", id);
	}
}
