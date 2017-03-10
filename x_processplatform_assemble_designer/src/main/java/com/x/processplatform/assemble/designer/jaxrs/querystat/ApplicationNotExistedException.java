package com.x.processplatform.assemble.designer.jaxrs.querystat;

import com.x.base.core.exception.PromptException;

class ApplicationNotExistedException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ApplicationNotExistedException(String flag) {
		super("application: {} not existed.", flag);
	}
}
