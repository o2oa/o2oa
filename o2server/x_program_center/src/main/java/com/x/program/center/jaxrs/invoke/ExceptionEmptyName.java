package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyName extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionEmptyName() {
		super("调用接口名称不能为空.");
	}
}
