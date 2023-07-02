package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPasswordEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public static String defaultMessage = "密码不能为空或者密码解密错误.";

	ExceptionPasswordEmpty() {
		super(defaultMessage);
	}
}
