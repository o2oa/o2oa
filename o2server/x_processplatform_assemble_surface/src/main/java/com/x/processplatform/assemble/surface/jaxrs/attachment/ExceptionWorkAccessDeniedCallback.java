package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionWorkAccessDeniedCallback extends CallbackPromptException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionWorkAccessDeniedCallback(String callbackName, String person, String title, String workId) {
		super(callbackName, "person:{} access work title:{} id:{}, denied.", person, title, workId);
	}

}
