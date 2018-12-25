package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionCaptchaEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCaptchaEmpty() {
		super("图片验证码不能为空.");
	}
}