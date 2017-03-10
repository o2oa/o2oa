package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.exception.PromptException;

class TaskNotExistedException extends PromptException {

	private static final long serialVersionUID = -2925120598877380881L;

	TaskNotExistedException(String flag) {
		super("task: {} not existed.", flag);
	}
}
