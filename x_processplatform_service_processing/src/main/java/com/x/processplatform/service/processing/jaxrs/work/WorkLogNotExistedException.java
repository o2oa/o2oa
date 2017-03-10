package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.exception.PromptException;

class WorkLogNotExistedException extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	WorkLogNotExistedException(String workId) {
		super("workLog id:{}, not existed.", workId);
	}

}
