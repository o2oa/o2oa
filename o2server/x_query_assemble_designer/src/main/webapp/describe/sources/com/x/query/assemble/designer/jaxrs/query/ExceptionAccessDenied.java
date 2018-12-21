package com.x.query.assemble.designer.jaxrs.query;

import com.x.base.core.project.exception.PromptException;

class ExceptionAccessDenied extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionAccessDenied(String person) {
		super("用户:{} 访问被拒绝.", person);
	}

}
