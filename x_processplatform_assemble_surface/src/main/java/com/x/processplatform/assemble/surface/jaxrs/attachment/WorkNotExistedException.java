package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.exception.PromptException;

class WorkNotExistedException extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	WorkNotExistedException(String workId) {
		super("work id:{}, not existed.", workId);
	}

}
