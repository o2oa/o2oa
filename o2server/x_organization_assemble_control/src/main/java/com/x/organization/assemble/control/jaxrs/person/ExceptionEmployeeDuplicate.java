package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmployeeDuplicate extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionEmployeeDuplicate(String name, String fieldName) {
		super("用户邮箱错误:{}, {}已有值重复.", name, fieldName);
	}
}
