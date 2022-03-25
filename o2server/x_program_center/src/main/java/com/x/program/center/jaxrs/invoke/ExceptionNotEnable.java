package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNotEnable extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionNotEnable(String name) {
		super("调用接口: {} 未启用.", name);
	}
}
