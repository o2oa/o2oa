package com.x.cms.assemble.control.jaxrs.templateform;

import com.x.base.core.project.exception.PromptException;

class ExceptionFormNotExist extends PromptException {

	private static final long serialVersionUID = 6984800093761853101L;

	ExceptionFormNotExist(String id) {
		super("form id:{}, not existed,", id);
	}

}
