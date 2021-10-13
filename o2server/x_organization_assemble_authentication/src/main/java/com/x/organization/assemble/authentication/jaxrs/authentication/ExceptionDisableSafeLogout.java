package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDisableSafeLogout extends LanguagePromptException {

	private static final long serialVersionUID = 6351023802034208595L;

	public static String defaultMessage = "系统没有启用安全注销功能.";

	ExceptionDisableSafeLogout() {
		super(defaultMessage);
	}
}
