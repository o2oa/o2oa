package com.x.processplatform.assemble.surface.jaxrs.applicationdict;

import com.x.base.core.project.exception.PromptException;

class ExceptionAccessDenied extends PromptException {

	private static final long serialVersionUID = -5954915325716358186L;

	ExceptionAccessDenied(String name) {
		super("用户:" + name + ", 没有足够的权限.");
	}

}
