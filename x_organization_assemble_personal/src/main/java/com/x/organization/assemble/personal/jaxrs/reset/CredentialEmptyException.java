package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.exception.PromptException;

public class CredentialEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public CredentialEmptyException() {
		super("用户标识不能为空.");
	}
}
