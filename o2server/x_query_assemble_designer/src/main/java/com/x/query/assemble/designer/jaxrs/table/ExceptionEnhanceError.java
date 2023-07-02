package com.x.query.assemble.designer.jaxrs.table;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEnhanceError extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionEnhanceError(String out) {
		super("增强失败:{}.", out);
	}
}
