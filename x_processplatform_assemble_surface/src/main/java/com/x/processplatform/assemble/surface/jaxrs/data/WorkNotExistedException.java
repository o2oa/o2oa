package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.base.core.exception.PromptException;

class WorkNotExistedException extends PromptException {

	private static final long serialVersionUID = -7694989472598070817L;

	WorkNotExistedException(String workId) {
		super("work id:{}, not existed.", workId);
	}
}
