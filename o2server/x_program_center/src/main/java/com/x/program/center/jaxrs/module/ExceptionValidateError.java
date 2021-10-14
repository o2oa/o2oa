package com.x.program.center.jaxrs.module;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionValidateError extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionValidateError() {
		super("云服务器帐号/密码错误.");
	}
}
