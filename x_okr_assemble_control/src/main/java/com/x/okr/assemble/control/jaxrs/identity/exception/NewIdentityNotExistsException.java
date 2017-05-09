package com.x.okr.assemble.control.jaxrs.identity.exception;

import com.x.base.core.exception.PromptException;

public class NewIdentityNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public NewIdentityNotExistsException( String identity ) {
		super("用户身份不存在。identity:" + identity );
	}
}
