package com.x.processplatform.assemble.designer.jaxrs.output;

import com.x.base.core.project.exception.PromptException;

class ExceptionApplicationAccessDenied extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionApplicationAccessDenied(String person, String name, String id) {
		super("person:{} access application name: {} id: {}, denied.", person, name, id);
	}

}
