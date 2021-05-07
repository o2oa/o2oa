package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidName extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionInvalidName(String name) {
		super("用户名错误,不能空,且不能使用特殊字符:{}.", name);
	}
}
