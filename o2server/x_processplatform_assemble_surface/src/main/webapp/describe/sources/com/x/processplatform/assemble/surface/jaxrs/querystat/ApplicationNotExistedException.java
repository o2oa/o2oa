package com.x.processplatform.assemble.surface.jaxrs.querystat;

import com.x.base.core.project.exception.PromptException;

class ApplicationNotExistedException extends PromptException {

	private static final long serialVersionUID = 5092496738469805434L;

	ApplicationNotExistedException(String flag) {
		super("application:{} not existed.", flag);
	}
}
