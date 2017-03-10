package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.exception.PromptException;

class CaptchaEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CaptchaEmptyException() {
		super("图片验证码不能为空.");
	}
}