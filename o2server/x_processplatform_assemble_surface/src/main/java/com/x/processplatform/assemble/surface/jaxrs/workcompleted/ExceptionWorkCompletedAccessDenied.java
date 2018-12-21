package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkCompletedAccessDenied extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionWorkCompletedAccessDenied(String person, String str) {
		super("person:{} access workCompleted: {}, denied.", person, str);
	}

}