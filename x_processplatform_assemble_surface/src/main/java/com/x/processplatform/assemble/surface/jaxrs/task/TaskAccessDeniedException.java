package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.exception.PromptException;

class TaskAccessDeniedException extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	TaskAccessDeniedException(String person, String id) {
		super("person:{} access task id:{}, denied.", person, id);
	}

}
