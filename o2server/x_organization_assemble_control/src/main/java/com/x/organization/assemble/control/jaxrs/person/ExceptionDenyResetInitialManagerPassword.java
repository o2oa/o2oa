package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDenyResetInitialManagerPassword extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyResetInitialManagerPassword() {
		super("无法重置初始管理员密码.");
	}
}
