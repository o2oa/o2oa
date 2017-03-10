package com.x.processplatform.assemble.surface.jaxrs.read;

import com.x.base.core.exception.PromptException;

class ReadAccessDeniedException extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ReadAccessDeniedException(String person, String readId) {
		super("person:{} access read id:{}, denied.", person, readId);
	}

}
