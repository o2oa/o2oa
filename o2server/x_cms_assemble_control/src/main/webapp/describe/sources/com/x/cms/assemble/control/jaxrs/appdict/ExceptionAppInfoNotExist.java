package com.x.cms.assemble.control.jaxrs.appdict;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppInfoNotExist extends PromptException {

	private static final long serialVersionUID = -5954915325716358186L;

	ExceptionAppInfoNotExist(String flag) {
		super("标识为:" + flag + ", 的应用不存在");
	}

}
