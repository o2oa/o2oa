package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.exception.PromptException;

class WorkAccessDeniedException extends PromptException {

	private static final long serialVersionUID = 9085364457175859374L;

	WorkAccessDeniedException(String person, String title, String workId) {
		super("person:{} access work title:{} id:{}, denied.", person, title, workId);
	}

}
