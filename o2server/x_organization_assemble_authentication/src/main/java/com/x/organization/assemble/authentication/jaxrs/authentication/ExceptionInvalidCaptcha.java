package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidCaptcha extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionInvalidCaptcha() {
		super("图片验证码错误.");
	}
}
