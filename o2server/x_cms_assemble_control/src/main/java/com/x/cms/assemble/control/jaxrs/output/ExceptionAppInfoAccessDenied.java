package com.x.cms.assemble.control.jaxrs.output;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppInfoAccessDenied extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionAppInfoAccessDenied(String person, String name, String id) {
		super("person:{} access appInfo name: {} id: {}, denied.", person, name, id);
	}

}
