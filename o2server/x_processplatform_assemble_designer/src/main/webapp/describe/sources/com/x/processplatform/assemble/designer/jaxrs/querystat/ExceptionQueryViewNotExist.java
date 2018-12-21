package com.x.processplatform.assemble.designer.jaxrs.querystat;

import com.x.base.core.project.exception.PromptException;

class ExceptionQueryViewNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionQueryViewNotExist(String flag) {
		super("queryView: {} not existed.", flag);
	}
}
