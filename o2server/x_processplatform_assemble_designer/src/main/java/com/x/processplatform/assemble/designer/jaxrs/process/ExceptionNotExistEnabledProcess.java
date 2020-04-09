package com.x.processplatform.assemble.designer.jaxrs.process;

import com.x.base.core.project.exception.PromptException;

class ExceptionNotExistEnabledProcess extends PromptException {

	private static final long serialVersionUID = 7770778192986529177L;

	ExceptionNotExistEnabledProcess(String flag) {
		super("process: {} in all edition not exist enabled process.", flag);
	}
}
