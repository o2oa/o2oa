package com.x.program.center.jaxrs.agent;

import com.x.base.core.project.exception.PromptException;

class ExceptionDuplicateName extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionDuplicateName(String name) {
		super("名称: {} 重复.", name);
	}
}
