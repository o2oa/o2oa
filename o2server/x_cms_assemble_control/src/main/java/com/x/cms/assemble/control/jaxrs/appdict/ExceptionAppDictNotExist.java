package com.x.cms.assemble.control.jaxrs.appdict;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppDictNotExist extends PromptException {

	private static final long serialVersionUID = -5954915325716358186L;

	ExceptionAppDictNotExist(String flag) {
		super("标识为:" + flag + ", 的数据字典不存在");
	}

}
