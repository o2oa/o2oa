package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkNotExist extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionWorkNotExist(String workId) {
		super("work id:{}, not existed.", workId);
	}

}
