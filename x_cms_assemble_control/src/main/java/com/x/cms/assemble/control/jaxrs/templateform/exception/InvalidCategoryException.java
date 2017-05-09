package com.x.cms.assemble.control.jaxrs.templateform.exception;

import com.x.base.core.exception.PromptException;

public class InvalidCategoryException extends PromptException {

	private static final long serialVersionUID = 6984800093761853101L;

	public InvalidCategoryException(String category) {
		super("category must be simply string:{}.", category);
	}

}
