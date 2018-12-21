package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.PromptException;

 class ExceptionNameDuplicate extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	 ExceptionNameDuplicate(String name, String fieldName) {
		super("用户名错误:" + name + ", " + fieldName + "已有值重复.");
	}
}
