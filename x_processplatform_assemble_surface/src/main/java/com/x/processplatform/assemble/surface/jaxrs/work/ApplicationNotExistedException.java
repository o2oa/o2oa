package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.exception.PromptException;

class ApplicationNotExistedException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ApplicationNotExistedException(String str) {
		super("application: {} not existed.", str);
	}
}
