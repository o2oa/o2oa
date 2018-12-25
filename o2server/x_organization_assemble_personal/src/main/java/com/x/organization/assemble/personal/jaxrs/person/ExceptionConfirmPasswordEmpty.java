package com.x.organization.assemble.personal.jaxrs.person;

import com.x.base.core.project.exception.PromptException;

class ExceptionConfirmPasswordEmpty extends PromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionConfirmPasswordEmpty() {
		super("确认密码不能为空.");
	}

}
