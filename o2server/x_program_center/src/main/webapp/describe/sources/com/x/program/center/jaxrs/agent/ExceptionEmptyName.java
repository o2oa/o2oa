package com.x.program.center.jaxrs.agent;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyName extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionEmptyName() {
		super("名称不能为空.");
	}
}
