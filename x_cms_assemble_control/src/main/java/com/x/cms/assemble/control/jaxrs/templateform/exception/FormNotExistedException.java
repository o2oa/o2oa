package com.x.cms.assemble.control.jaxrs.templateform.exception;

import com.x.base.core.exception.PromptException;

public class FormNotExistedException extends PromptException {

	private static final long serialVersionUID = 6984800093761853101L;

	public FormNotExistedException(String id) {
		super("form id:{}, not existed,", id);
	}

}
