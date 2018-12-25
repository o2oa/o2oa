package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkCompletedNotExist extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionWorkCompletedNotExist(String workCompletedId) {
		super("workCompleted id:{}, not existed.", workCompletedId);
	}

}
