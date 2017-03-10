package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.base.core.exception.PromptException;

class WorkCompletedAccessDeniedException extends PromptException {

	private static final long serialVersionUID = -665095222445791960L;

	WorkCompletedAccessDeniedException(String person, String title, String workCompletedId) {
		super("person:{}, read workCompleted title:{} id:{}, was denied because of insufficient permission.", person, title,
				workCompletedId);
	}
}
