package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionIdentityNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionIdentityNotExist(String str) {
		super("身份不存在:{}", str);
	}
}
