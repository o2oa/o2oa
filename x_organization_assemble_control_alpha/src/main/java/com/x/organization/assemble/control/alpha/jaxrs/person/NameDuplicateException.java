package com.x.organization.assemble.control.alpha.jaxrs.person;

import com.x.base.core.exception.PromptException;

 class NameDuplicateException extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	 NameDuplicateException(String name, String fieldName) {
		super("用户名错误:" + name + ", " + fieldName + "已有值重复.");
	}
}
