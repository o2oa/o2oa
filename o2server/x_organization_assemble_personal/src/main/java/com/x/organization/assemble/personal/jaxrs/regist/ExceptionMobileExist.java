package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionMobileExist extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionMobileExist(String mobile) {
		super("手机:{}已注册.", mobile);
	}
}
