package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.exception.PromptException;

class NotManagerException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	NotManagerException(String str) {
		super("person: {} not has Manager role.", str);
	}
}
