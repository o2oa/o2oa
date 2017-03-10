package com.x.processplatform.assemble.surface.jaxrs.readcompleted;

import com.x.base.core.exception.PromptException;

class ReadCompletedNotExistedException extends PromptException {

	private static final long serialVersionUID = -2925120598877380881L;

	ReadCompletedNotExistedException(String flag) {
		super("readCompleted: {} not existed.", flag);
	}
}
