package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.exception.PromptException;

class DisableRegistException extends PromptException {

	private static final long serialVersionUID = 6351023802034208595L;

	DisableRegistException() {
		super("系统不允许人员注册.");
	}
}
