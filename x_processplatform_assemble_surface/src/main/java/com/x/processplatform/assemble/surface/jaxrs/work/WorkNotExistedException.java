package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.exception.PromptException;

class WorkNotExistedException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	WorkNotExistedException(String str) {
		super("work: {} not existed.", str);
	}
}
