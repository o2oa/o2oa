package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.exception.PromptException;

class WorkCompletedAccessDeniedException extends PromptException {

	private static final long serialVersionUID = 9085364457175859374L;

	WorkCompletedAccessDeniedException(String person, String title, String workCompletedId) {
		super("person:{} access workCompleted title:{} id:{}, denied.", person, title, workCompletedId);
	}

}
