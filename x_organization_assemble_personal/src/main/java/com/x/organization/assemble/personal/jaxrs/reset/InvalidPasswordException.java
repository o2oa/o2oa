package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.exception.PromptException;

public class InvalidPasswordException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public InvalidPasswordException() {
		super("用户名,密码不正确.");
	}
}
