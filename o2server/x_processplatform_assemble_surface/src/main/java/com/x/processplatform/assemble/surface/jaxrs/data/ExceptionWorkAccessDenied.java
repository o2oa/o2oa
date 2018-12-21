package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkAccessDenied extends PromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionWorkAccessDenied(String person, String title, String workId) {
		super("person:{}, read work title:{} id:{}, was denied because of insufficient permission.", person, title,
				workId);
	}
}
