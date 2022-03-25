package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDuplicateAlias extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionDuplicateAlias(String name) {
		super("调用接口别名: {} 重复.", name);
	}
}
