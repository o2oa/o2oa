package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.exception.PromptException;

class ExceptionDisableRegist extends PromptException {

	private static final long serialVersionUID = 6351023802034208595L;

	ExceptionDisableRegist() {
		super("系统不允许人员注册.");
	}
}
