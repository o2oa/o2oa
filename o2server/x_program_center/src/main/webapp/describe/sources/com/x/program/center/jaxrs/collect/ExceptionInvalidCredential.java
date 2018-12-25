package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidCredential extends PromptException {

	private static final long serialVersionUID = 5173172412837627670L;

	ExceptionInvalidCredential() {
		super("用户名密码错误,无法和注册服务器通讯.");
	}
}
