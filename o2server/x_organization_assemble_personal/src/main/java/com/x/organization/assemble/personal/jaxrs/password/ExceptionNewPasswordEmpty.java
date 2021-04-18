package com.x.organization.assemble.personal.jaxrs.password;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNewPasswordEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionNewPasswordEmpty() {
		super("新密码不能为空.");
	}

}
