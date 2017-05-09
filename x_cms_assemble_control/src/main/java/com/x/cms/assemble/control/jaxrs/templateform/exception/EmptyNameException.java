package com.x.cms.assemble.control.jaxrs.templateform.exception;

import com.x.base.core.exception.PromptException;

public class EmptyNameException extends PromptException {

	private static final long serialVersionUID = 6984800093761853101L;

	public EmptyNameException() {
		super("name can not be empty.");
	}

}
