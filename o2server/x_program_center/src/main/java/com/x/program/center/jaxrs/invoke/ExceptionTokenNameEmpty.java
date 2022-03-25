package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionTokenNameEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionTokenNameEmpty() {
		super("令牌错误,名称为空.");
	}
}
