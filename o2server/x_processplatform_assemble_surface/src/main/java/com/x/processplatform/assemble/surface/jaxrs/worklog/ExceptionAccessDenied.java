package com.x.processplatform.assemble.surface.jaxrs.worklog;

import com.x.base.core.project.exception.PromptException;

class ExceptionAccessDenied extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionAccessDenied(String person) {
		super("用户:{} 权限不足.", person);
	}

}
