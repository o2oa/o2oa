package com.x.organization.assemble.personal.jaxrs.empower;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionTypeAllExist extends LanguagePromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionTypeAllExist(String identity) {
		super("身份 {} 的全局设置已经存在.", identity);
	}

}
