package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidCode extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionInvalidCode() {
		super("手机验证码错误.");
	}
}
