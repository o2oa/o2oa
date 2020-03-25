package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionProcessNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionProcessNotExist(String flag) {
		super("process: {} not existed.", flag);
	}
}
