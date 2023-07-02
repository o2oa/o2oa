package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPasswordEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionPasswordEmpty() {
		super("密码不能为空.");
	}
}
