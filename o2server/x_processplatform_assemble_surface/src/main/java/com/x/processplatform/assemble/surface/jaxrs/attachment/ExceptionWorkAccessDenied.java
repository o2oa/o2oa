package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkAccessDenied extends PromptException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionWorkAccessDenied(String person, String title, String workId) {
		super("person:{} access work title:{} id:{}, denied.", person, title, workId);
	}

}
