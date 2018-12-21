package com.x.processplatform.assemble.surface.jaxrs.applicationdict;

import com.x.base.core.project.exception.PromptException;

class ExceptionApplicationNotExist extends PromptException {

	private static final long serialVersionUID = -5954915325716358186L;

	ExceptionApplicationNotExist(String flag) {
		super("标识为:" + flag + ", 的应用不存在");
	}

}
