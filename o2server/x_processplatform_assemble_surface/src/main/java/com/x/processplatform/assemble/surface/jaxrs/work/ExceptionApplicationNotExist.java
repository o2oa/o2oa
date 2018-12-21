package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionApplicationNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionApplicationNotExist(String str) {
		super("application: {} not existed.", str);
	}
}
