package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionNotManager extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionNotManager(String str) {
		super("person: {} not has Manager role.", str);
	}
}
