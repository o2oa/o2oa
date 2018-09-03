package com.x.cms.assemble.control.jaxrs.templateform;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidCategory extends PromptException {

	private static final long serialVersionUID = 6984800093761853101L;

	ExceptionInvalidCategory(String category) {
		super("category must be simply string:{}.", category);
	}

}
