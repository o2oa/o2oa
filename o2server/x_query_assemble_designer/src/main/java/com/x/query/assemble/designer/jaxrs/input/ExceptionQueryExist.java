package com.x.query.assemble.designer.jaxrs.input;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionQueryExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionQueryExist(String flag) {
		super("应用: {} 已存在.", flag);
	}
}
