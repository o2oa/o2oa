package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonNotExistOrInvalidAnswer extends PromptException {

	private static final long serialVersionUID = -8334021007462970656L;
	public static String defaultMessage = "用户不存在或者验证码错误.";

	ExceptionPersonNotExistOrInvalidAnswer( ) {
		super(defaultMessage);
	}
}
