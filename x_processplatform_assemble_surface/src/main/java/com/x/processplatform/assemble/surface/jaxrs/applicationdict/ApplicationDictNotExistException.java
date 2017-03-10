package com.x.processplatform.assemble.surface.jaxrs.applicationdict;

import com.x.base.core.exception.PromptException;

class ApplicationDictNotExistException extends PromptException {

	private static final long serialVersionUID = -5954915325716358186L;

	ApplicationDictNotExistException(String flag) {
		super("标识为:" + flag + ", 的数据字典不存在");
	}

}
