package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.PromptException;

class ExceptionRoleNotExist extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionRoleNotExist(String name) {
		super("角色: {} 不存在.", name);
	}
}
