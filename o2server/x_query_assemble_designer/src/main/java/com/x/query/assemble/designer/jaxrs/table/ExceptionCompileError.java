package com.x.query.assemble.designer.jaxrs.table;

import com.x.base.core.project.exception.PromptException;

class ExceptionCompileError extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionCompileError(String out) {
		super("编译失败:{}.", out);
	}
}
