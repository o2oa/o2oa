package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDisableRegist extends LanguagePromptException {

	private static final long serialVersionUID = 6351023802034208595L;

	ExceptionDisableRegist() {
		super("系统不允许人员注册.");
	}
}
