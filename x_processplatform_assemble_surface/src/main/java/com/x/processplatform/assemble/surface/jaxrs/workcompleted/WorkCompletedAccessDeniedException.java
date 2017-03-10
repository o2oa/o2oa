package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import com.x.base.core.exception.PromptException;

class WorkCompletedAccessDeniedException extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	WorkCompletedAccessDeniedException(String person, String str) {
		super("person:{} access workCompleted: {}, denied.", person, str);
	}

}