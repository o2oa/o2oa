package com.x.cms.assemble.control.jaxrs.output;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppDictNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionAppDictNotExist(String flag) {
		super("form: {} not existed.", flag);
	}
}
