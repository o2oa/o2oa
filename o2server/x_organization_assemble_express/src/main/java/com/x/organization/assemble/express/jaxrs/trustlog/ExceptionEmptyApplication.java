package com.x.organization.assemble.express.jaxrs.trustlog;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyApplication extends PromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionEmptyApplication() {
		super("应用不能为空.");
	}
}
