package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonNotExistOrInvalidPassword extends PromptException {


	private static final long serialVersionUID = 2537120821114609351L;
	public static String defaultMessage = "用户不存在或者密码错误.";

	ExceptionPersonNotExistOrInvalidPassword( ) {
		super(defaultMessage);
	}
}
