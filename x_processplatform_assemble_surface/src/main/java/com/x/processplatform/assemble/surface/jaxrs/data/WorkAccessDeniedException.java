package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.base.core.exception.PromptException;

class WorkAccessDeniedException extends PromptException {

	private static final long serialVersionUID = -665095222445791960L;

	WorkAccessDeniedException(String person, String title, String workId) {
		super("person:{}, read work title:{} id:{}, was denied because of insufficient permission.", person, title,
				workId);
	}
}
