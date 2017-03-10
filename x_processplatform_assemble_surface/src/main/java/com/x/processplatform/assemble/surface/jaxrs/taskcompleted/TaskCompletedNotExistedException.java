package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import com.x.base.core.exception.PromptException;

class TaskCompletedNotExistedException extends PromptException {

	private static final long serialVersionUID = -2925120598877380881L;

	TaskCompletedNotExistedException(String flag) {
		super("taskCompleted: {} not existed.", flag);
	}
}
