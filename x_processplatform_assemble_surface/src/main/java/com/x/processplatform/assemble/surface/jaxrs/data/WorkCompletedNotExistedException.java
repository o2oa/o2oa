package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.base.core.exception.PromptException;

class WorkCompletedNotExistedException extends PromptException {

	private static final long serialVersionUID = -7694989472598070817L;

	WorkCompletedNotExistedException(String workId) {
		super("workCompleted id:{}, not existed.", workId);
	}
}
