package com.x.processplatform.assemble.surface.jaxrs.applicationdict;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionApplicationDictNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -5954915325716358186L;

	ExceptionApplicationDictNotExist(String flag) {
		super("标识为:{}, 的数据字典不存在", flag);
	}

}
