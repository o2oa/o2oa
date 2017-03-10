package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.exception.PromptException;

class InvalidCodeException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InvalidCodeException() {
		super("手机验证码错误.");
	}
}
