package com.x.processplatform.assemble.surface.jaxrs.serialnumber;

import com.x.base.core.exception.PromptException;

class ApplicationAccessDeniedException extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ApplicationAccessDeniedException(String person, String id) {
		super("person:{} access application id:{}, denied.", person, id);
	}

}
