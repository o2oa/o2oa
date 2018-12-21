package com.x.program.center.jaxrs.module;

import com.x.base.core.project.exception.PromptException;

class ExceptionValidateError extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionValidateError() {
		super("云服务器帐号/密码错误.");
	}
}
