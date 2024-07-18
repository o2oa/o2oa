package com.x.program.center.jaxrs.adminlogin;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonNotExistOrInvalidPassword extends PromptException {


	private static final long serialVersionUID = 2537120821114609351L;
	private static final String DEFAULT_MESSAGE = "用户不存在或者密码错误.";

	ExceptionPersonNotExistOrInvalidPassword( ) {
		super(DEFAULT_MESSAGE);
	}
}
