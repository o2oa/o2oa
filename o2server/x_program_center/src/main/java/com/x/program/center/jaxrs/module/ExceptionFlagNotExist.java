package com.x.program.center.jaxrs.module;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionFlagNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionFlagNotExist(String flag) {
		super("flag:{}不存在.", flag);
	}
}
