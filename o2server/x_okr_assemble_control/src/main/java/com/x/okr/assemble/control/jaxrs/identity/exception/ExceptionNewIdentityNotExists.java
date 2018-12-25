package com.x.okr.assemble.control.jaxrs.identity.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionNewIdentityNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionNewIdentityNotExists( String identity ) {
		super("用户身份不存在。identity:" + identity );
	}
}
