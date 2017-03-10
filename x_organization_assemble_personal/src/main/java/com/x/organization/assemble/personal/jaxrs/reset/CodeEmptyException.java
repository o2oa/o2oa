package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.exception.PromptException;

public class CodeEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public CodeEmptyException() {
		super("短信验证码不能为空.");
	}
}
