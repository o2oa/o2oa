package com.x.query.assemble.designer.jaxrs.table;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionCompileError extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	public ExceptionCompileError(String out) {
		super("编译失败:{}.", out);
	}
}
