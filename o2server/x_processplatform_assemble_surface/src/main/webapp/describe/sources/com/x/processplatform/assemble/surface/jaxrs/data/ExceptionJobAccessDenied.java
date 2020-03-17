package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.base.core.project.exception.PromptException;

class ExceptionJobAccessDenied extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionJobAccessDenied(String person, String str) {
		super("person:{} access job: {}, denied.", person, str);
	}

}
