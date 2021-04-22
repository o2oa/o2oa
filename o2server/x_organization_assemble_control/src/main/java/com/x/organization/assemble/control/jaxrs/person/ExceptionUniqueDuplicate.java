package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

 class ExceptionUniqueDuplicate extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	 ExceptionUniqueDuplicate(String name, String fieldName) {
		super("用户唯一编码错误:{}, {}已有值重复.", name, fieldName);
	}
}
