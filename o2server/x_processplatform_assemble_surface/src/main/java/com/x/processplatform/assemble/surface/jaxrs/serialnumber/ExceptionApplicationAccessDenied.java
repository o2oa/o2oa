package com.x.processplatform.assemble.surface.jaxrs.serialnumber;

import com.x.base.core.project.exception.PromptException;

class ExceptionApplicationAccessDenied extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionApplicationAccessDenied(String person, String id) {
		super("person:{} access application id:{}, denied.", person, id);
	}

}
