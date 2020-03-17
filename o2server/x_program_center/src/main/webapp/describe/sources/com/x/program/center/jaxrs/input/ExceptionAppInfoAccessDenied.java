package com.x.program.center.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppInfoAccessDenied extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionAppInfoAccessDenied(String person, String name, String id) {
		super("person:{} access application name: {} id: {}, denied.", person, name, id);
	}

}
