package com.x.organization.assemble.personal.jaxrs.password;

import com.x.base.core.project.exception.PromptException;

class ExceptionOldPasswordEmpty extends PromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionOldPasswordEmpty() {
		super("无效的操作请求.");
	}

}
