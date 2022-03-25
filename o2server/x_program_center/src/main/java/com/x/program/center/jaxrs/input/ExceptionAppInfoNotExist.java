package com.x.program.center.jaxrs.input;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppInfoNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionAppInfoNotExist(String flag) {
		super("应用名称:{}不存在。", flag);
	}
}
