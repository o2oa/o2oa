package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPersonNotExistOrInvalidPassword extends LanguagePromptException {

	private static final long serialVersionUID = -6124481323896411121L;

	public static String defaultMessage = "用户不存在或者密码错误.";

	ExceptionPersonNotExistOrInvalidPassword( ) {
		super(defaultMessage);
	}
}
