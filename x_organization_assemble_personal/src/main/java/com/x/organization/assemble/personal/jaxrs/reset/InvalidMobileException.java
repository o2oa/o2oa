package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.exception.PromptException;

public class InvalidMobileException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public InvalidMobileException() {
		super("用户注册的手机号不正确.");
	}
}
