package com.x.cms.assemble.control.jaxrs.templateform;

import com.x.base.core.exception.PromptException;

class FormNotExistedException extends PromptException {

	private static final long serialVersionUID = 6984800093761853101L;

	FormNotExistedException(String id) {
		super("form id:{}, not existed,", id);
	}

}
