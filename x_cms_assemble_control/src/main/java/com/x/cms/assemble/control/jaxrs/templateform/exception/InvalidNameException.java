package com.x.cms.assemble.control.jaxrs.templateform.exception;

import com.x.base.core.exception.PromptException;

public class InvalidNameException extends PromptException {

	private static final long serialVersionUID = 6984800093761853101L;

	public InvalidNameException(String name) {
		super("name must be simply string:{}.", name);
	}

}
