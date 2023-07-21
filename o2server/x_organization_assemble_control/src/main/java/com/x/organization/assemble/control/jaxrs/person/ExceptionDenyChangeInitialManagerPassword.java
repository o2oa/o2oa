package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDenyChangeInitialManagerPassword extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyChangeInitialManagerPassword() {
		super("请通过控制台修改初始管理员密码.");
	}
}
