package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import com.x.base.core.exception.PromptException;

class ProcessNotExistedException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ProcessNotExistedException(String flag) {
		super("process: {} not existed.", flag);
	}
}
