package com.x.organization.assemble.personal.jaxrs.password;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEditInitialManagerDeny extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionEditInitialManagerDeny() {
		super("不能更新初始管理员.");
	}
}
