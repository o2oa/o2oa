package com.x.processplatform.assemble.designer.jaxrs.templateform;

import com.x.base.core.exception.PromptException;

class InvalidNameException extends PromptException {

	private static final long serialVersionUID = 6984800093761853101L;

	InvalidNameException(String name) {
		super("name must be simply string:{}.", name);
	}

}
