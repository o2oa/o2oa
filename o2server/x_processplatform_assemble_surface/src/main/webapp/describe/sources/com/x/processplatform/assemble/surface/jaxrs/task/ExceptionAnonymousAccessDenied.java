package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class ExceptionAnonymousAccessDenied extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionAnonymousAccessDenied() {
		super("匿名访问被拒绝.");
	}

}
