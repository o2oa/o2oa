package com.x.processplatform.assemble.surface.jaxrs.read;

import com.x.base.core.exception.PromptException;

class ReadNotExistedException extends PromptException {

	private static final long serialVersionUID = -2925120598877380881L;

	ReadNotExistedException(String flag) {
		super("read: {} not existed.", flag);
	}
}
