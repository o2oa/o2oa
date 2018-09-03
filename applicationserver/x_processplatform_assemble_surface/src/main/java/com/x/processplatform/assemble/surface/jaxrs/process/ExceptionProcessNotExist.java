package com.x.processplatform.assemble.surface.jaxrs.process;

import com.x.base.core.project.exception.PromptException;

class ExceptionProcessNotExist extends PromptException {

	private static final long serialVersionUID = 7546873370819826009L;

	ExceptionProcessNotExist(String flag) {
		super("process:{} not existed.", flag);
	}
}
