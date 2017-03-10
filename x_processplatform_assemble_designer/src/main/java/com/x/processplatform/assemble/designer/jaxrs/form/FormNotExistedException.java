package com.x.processplatform.assemble.designer.jaxrs.form;

import com.x.base.core.exception.PromptException;

class FormNotExistedException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	FormNotExistedException(String flag) {
		super("form: {} not existed.", flag);
	}
}
