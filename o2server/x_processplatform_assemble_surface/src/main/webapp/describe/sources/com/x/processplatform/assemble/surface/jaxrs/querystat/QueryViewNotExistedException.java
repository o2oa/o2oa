package com.x.processplatform.assemble.surface.jaxrs.querystat;

import com.x.base.core.project.exception.PromptException;

class QueryViewNotExistedException extends PromptException {

	private static final long serialVersionUID = 1633835400422042028L;

	QueryViewNotExistedException(String flag, String applicationFlag) {
		super("queryView: {}, not existed in application: {}.", flag, applicationFlag);
	}
}
