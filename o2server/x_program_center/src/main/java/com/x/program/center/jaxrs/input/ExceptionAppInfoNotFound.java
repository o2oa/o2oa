package com.x.program.center.jaxrs.input;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppInfoNotFound extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionAppInfoNotFound(String name, String id, String alias) {
		super("应用程序名称{}，id:{}，别名:{}未找到。", name, id, alias);
	}
}
