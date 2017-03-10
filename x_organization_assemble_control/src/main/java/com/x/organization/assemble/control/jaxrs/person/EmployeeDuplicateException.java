package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.exception.PromptException;

 class EmployeeDuplicateException extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	 EmployeeDuplicateException(String name, String fieldName) {
		super("用户名错误:" + name + ", " + fieldName + "已有值重复.");
	}
}
