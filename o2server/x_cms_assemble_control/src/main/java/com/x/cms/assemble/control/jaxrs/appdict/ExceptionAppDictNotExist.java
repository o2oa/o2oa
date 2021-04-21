package com.x.cms.assemble.control.jaxrs.appdict;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppDictNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -5954915325716358186L;

	ExceptionAppDictNotExist(String flag) {
		super("标识为:{}, 的数据字典不存在.", flag);
	}

}
