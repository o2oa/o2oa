package com.x.processplatform.assemble.designer.jaxrs.application;

import com.x.base.core.project.exception.PromptException;

class ExceptionApplicationDictNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionApplicationDictNotExist(String flag) {
		super("form: {} not existed.", flag);
	}
}
