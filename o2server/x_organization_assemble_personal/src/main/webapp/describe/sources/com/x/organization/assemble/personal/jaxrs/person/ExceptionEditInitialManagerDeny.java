package com.x.organization.assemble.personal.jaxrs.person;

import com.x.base.core.project.exception.PromptException;

class ExceptionEditInitialManagerDeny extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionEditInitialManagerDeny() {
		super("不能更新初始管理员.");
	}
}
