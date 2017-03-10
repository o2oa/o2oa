package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.exception.PromptException;

public class PasswordEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public PasswordEmptyException() {
		super("密码不能为空.");
	}
}
