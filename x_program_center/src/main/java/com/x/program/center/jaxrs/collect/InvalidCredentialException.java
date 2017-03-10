package com.x.program.center.jaxrs.collect;

import com.x.base.core.exception.PromptException;

class InvalidCredentialException extends PromptException {

	private static final long serialVersionUID = 5173172412837627670L;

	InvalidCredentialException() {
		super("用户名密码错误,无法和注册服务器通讯.");
	}
}
