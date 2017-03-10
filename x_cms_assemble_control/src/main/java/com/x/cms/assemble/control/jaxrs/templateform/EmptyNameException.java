package com.x.cms.assemble.control.jaxrs.templateform;

import com.x.base.core.exception.PromptException;

class EmptyNameException extends PromptException {

	private static final long serialVersionUID = 6984800093761853101L;

	EmptyNameException() {
		super("name can not be empty.");
	}

}
