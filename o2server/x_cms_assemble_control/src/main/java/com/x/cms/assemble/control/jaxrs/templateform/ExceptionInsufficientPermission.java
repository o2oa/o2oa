package com.x.cms.assemble.control.jaxrs.templateform;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInsufficientPermission extends LanguagePromptException {

	private static final long serialVersionUID = 1148555249431355284L;

	ExceptionInsufficientPermission(String person) {
		super("用户:{} 没有权限进行此操作.", person);
	}
}
