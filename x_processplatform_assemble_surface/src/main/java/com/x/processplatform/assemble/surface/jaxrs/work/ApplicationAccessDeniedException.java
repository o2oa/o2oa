package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.exception.PromptException;

class ApplicationAccessDeniedException extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ApplicationAccessDeniedException(String person, String str) {
		super("person:{} access application: {}, denied.", person, str);
	}

}
