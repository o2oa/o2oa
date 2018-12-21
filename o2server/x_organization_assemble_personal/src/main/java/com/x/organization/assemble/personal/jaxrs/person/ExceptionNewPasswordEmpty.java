package com.x.organization.assemble.personal.jaxrs.person;

import com.x.base.core.project.exception.PromptException;

class ExceptionNewPasswordEmpty extends PromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionNewPasswordEmpty() {
		super("新密码不能为空.");
	}

}
