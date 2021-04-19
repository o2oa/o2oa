package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNameExist extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionNameExist(String name) {
		super("用户:{}已注册.", name);
	}
}
