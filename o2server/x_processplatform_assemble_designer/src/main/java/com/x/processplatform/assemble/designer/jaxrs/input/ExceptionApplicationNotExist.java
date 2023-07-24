package com.x.processplatform.assemble.designer.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionApplicationNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionApplicationNotExist(String flag) {
		super("application: {} not existed.", flag);
	}
}
