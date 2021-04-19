package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionInvalidPassword extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionInvalidPassword(String hint) {
		super("不符合密码规则:{}.", hint);
	}
}
