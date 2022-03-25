package com.x.program.center.jaxrs.appstyle;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAliasExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionAliasExist(String str) {
		super("别名: {} 已存在.", str);
	}
}
