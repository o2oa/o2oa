package com.x.processplatform.assemble.designer.jaxrs.templateform;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidName extends PromptException {

	private static final long serialVersionUID = 6984800093761853101L;

	ExceptionInvalidName(String name) {
		super("name must be simply string:{}.", name);
	}

}
