package com.x.processplatform.assemble.designer.jaxrs.querystat;

import com.x.base.core.project.exception.PromptException;

class ExceptionQueryStatNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionQueryStatNotExist(String flag) {
		super("queryStat: {} not existed.", flag);
	}
}
