package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkLogNotExist extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionWorkLogNotExist(String workId) {
		super("workLog id:{}, not existed.", workId);
	}

}
