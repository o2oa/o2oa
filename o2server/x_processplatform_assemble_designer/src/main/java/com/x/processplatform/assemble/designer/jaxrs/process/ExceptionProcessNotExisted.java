package com.x.processplatform.assemble.designer.jaxrs.process;

import com.x.base.core.project.exception.PromptException;

class ExceptionProcessNotExisted extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionProcessNotExisted(String flag) {
		super("process: {} not existed.", flag);
	}
}
