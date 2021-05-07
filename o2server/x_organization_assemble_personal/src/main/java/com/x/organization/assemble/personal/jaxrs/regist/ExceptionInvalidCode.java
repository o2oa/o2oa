package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidCode extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionInvalidCode() {
		super("手机验证码错误.");
	}
}
