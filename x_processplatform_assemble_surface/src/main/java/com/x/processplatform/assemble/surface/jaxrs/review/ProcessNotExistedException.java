package com.x.processplatform.assemble.surface.jaxrs.review;

import com.x.base.core.exception.PromptException;

class ProcessNotExistedException extends PromptException {

	private static final long serialVersionUID = 7405822870669993030L;

	ProcessNotExistedException(String flag) {
		super("process: {} not existed.", flag);
	}
}
