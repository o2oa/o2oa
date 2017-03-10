package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import com.x.base.core.exception.PromptException;

class WorkCompletedNotExistedException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	WorkCompletedNotExistedException(String str) {
		super("workCompleted: {} not existed.", str);
	}
}
