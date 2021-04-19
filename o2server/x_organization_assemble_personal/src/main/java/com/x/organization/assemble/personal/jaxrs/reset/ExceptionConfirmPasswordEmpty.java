package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionConfirmPasswordEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionConfirmPasswordEmpty() {
		super("确认密码不能为空.");
	}

}
