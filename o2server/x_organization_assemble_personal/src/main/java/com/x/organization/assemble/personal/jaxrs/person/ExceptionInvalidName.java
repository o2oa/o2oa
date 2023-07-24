package com.x.organization.assemble.personal.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

 class ExceptionInvalidName extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	 public static String defaultMessage = "用户名错误,不能为空,不能使用保留字串,且不能使用特殊字符:{}.";

	 ExceptionInvalidName(String name) {
		super(defaultMessage, name);
	}
}
