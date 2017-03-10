package com.x.processplatform.assemble.surface.jaxrs.applicationdict;

import com.x.base.core.exception.PromptException;

class ApplicationNotExistException extends PromptException {

	private static final long serialVersionUID = -5954915325716358186L;

	ApplicationNotExistException(String flag) {
		super("标识为:" + flag + ", 的应用不存在");
	}

}
