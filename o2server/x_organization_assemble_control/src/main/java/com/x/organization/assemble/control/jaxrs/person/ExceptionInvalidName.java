package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

 class ExceptionInvalidName extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	 ExceptionInvalidName(String name) {
		super("用户名错误,不能为空,不能使用保留字串,且不能使用特殊字符:{}.", name);
	}
}
