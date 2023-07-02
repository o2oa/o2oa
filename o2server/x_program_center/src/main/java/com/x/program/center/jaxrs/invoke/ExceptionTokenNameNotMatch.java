package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionTokenNameNotMatch extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionTokenNameNotMatch(String name) {
		super("令牌名称不匹配:{}.", name);
	}
}
