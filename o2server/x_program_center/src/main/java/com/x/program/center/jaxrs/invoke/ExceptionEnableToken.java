package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEnableToken extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionEnableToken(String name) {
		super("接口已经启用了令牌认证,请使用令牌认证调用接口访问: {}.", name);
	}
}
