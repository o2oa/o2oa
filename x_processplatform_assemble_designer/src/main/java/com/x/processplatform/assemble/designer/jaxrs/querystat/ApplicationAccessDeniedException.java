package com.x.processplatform.assemble.designer.jaxrs.querystat;

import com.x.base.core.exception.PromptException;

class ApplicationAccessDeniedException extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ApplicationAccessDeniedException(String person, String name, String id) {
		super("person:{} access application name: {} id: {}, denied.", person, name, id);
	}

}
