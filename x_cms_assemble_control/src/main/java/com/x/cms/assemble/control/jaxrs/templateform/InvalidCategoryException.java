package com.x.cms.assemble.control.jaxrs.templateform;

import com.x.base.core.exception.PromptException;

class InvalidCategoryException extends PromptException {

	private static final long serialVersionUID = 6984800093761853101L;

	InvalidCategoryException(String category) {
		super("category must be simply string:{}.", category);
	}

}
