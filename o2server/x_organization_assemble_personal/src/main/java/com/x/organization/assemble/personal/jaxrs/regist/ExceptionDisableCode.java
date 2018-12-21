package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.exception.PromptException;

class ExceptionDisableCode extends PromptException {

	private static final long serialVersionUID = 6351023802034208595L;

	ExceptionDisableCode() {
		super("系统不允许通过手机验证注册人员.");
	}
}
