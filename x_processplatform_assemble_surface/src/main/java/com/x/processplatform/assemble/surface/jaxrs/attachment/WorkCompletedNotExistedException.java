package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.exception.PromptException;

class WorkCompletedNotExistedException extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	WorkCompletedNotExistedException(String workCompletedId) {
		super("workCompleted id:{}, not existed.", workCompletedId);
	}

}
