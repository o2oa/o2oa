package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.exception.PromptException;

public class InvalidCodeException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public InvalidCodeException() {
		super("手机验证码错误.");
	}
}
