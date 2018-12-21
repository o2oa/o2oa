package com.x.processplatform.assemble.surface.jaxrs.process;

import com.x.base.core.project.exception.PromptException;

class ExceptionApplicationNotExist extends PromptException {

	private static final long serialVersionUID = 5092496738469805434L;

	ExceptionApplicationNotExist(String flag) {
		super("application:{} not existed.", flag);
	}
}
