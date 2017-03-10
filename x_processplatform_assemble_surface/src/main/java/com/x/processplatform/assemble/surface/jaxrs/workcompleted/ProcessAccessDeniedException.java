package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import com.x.base.core.exception.PromptException;

class ProcessAccessDeniedException extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ProcessAccessDeniedException(String person, String str) {
		super("person:{} access process: {}, denied.", person, str);
	}

}
