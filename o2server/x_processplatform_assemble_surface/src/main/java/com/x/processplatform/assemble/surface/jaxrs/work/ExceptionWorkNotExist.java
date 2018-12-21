package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionWorkNotExist(String str) {
		super("work: {} not existed.", str);
	}
}
