package com.x.organization.assemble.personal.jaxrs.password;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonNotExisted extends PromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionPersonNotExisted(String name) {
		super("指定的用户:" + name + ", 不存在.");
	}

}
