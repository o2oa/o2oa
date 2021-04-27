package com.x.cms.assemble.control.jaxrs.appdictdesign;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppInfoNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionAppInfoNotExist(String flag) {
		super("指定的应用不存在:{}.", flag);
	}
}
