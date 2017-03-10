package com.x.processplatform.assemble.designer.jaxrs.querystat;

import com.x.base.core.exception.PromptException;

class QueryStatNotExistedException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	QueryStatNotExistedException(String flag) {
		super("queryStat: {} not existed.", flag);
	}
}
