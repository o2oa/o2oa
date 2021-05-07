package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidPassword extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionInvalidPassword(String hint) {
		super("不符合密码规则:{}.", hint);
	}
}
