package com.x.organization.assemble.control.jaxrs.identity;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionIdentityNotExist extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionIdentityNotExist(String flag) {
		super("身份:{}, 不存在.", flag);
	}
}
