package com.x.processplatform.assemble.surface.jaxrs.process;

import com.x.base.core.exception.PromptException;

class ProcessNotExistedException extends PromptException {

	private static final long serialVersionUID = 7546873370819826009L;

	ProcessNotExistedException(String flag) {
		super("process:{} not existed.", flag);
	}
}
