package com.x.processplatform.assemble.surface.jaxrs.querystat;

import com.x.base.core.project.exception.PromptException;

class QueryStatNotExistedException extends PromptException {

	private static final long serialVersionUID = -4720230175081531553L;

	QueryStatNotExistedException(String flag, String applicationFlag) {
		super("queryStat :{}, not existed in application: {}.", flag, applicationFlag);
	}
}
