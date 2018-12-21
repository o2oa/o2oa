package com.x.processplatform.assemble.surface.jaxrs.querystat;

import com.x.base.core.project.exception.PromptException;

class QueryViewAccessDeniedException extends PromptException {

	private static final long serialVersionUID = -3643751916412139045L;

	QueryViewAccessDeniedException(String person, String queryView, String application) {
		super("person:{} access queryView :{}, denied, in application: {}.", person, queryView, application);
	}

}
