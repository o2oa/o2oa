package com.x.processplatform.assemble.designer.jaxrs.templateform;

import com.x.base.core.project.exception.PromptException;

class ExceptionInsufficientPermission extends PromptException {

	private static final long serialVersionUID = 1148555249431355284L;

	ExceptionInsufficientPermission(String person) {
		super("用户:{} 无权限执行此操作.", person);
	}
}
