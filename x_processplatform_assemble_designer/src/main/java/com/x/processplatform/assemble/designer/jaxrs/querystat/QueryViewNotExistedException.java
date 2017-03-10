package com.x.processplatform.assemble.designer.jaxrs.querystat;

import com.x.base.core.exception.PromptException;

class QueryViewNotExistedException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	QueryViewNotExistedException(String flag) {
		super("queryView: {} not existed.", flag);
	}
}
