package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkAccessDenied extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionWorkAccessDenied(String person, String title, String id) {
		super("person:{} access work title :{}, id :{}, denied.", person, title, id);
	}

}
