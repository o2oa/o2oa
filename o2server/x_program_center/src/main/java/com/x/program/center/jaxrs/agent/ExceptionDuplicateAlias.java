package com.x.program.center.jaxrs.agent;

import com.x.base.core.project.exception.PromptException;

class ExceptionDuplicateAlias extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionDuplicateAlias(String name) {
		super("别名: {} 重复.", name);
	}
}
