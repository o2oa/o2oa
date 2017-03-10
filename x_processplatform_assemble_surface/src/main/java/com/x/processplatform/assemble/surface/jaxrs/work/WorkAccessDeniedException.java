package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.exception.PromptException;

class WorkAccessDeniedException extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	WorkAccessDeniedException(String person, String str) {
		super("person:{} access work: {}, denied.", person, str);
	}

}
