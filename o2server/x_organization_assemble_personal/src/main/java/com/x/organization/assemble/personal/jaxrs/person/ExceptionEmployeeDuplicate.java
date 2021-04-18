package com.x.organization.assemble.personal.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

 class ExceptionEmployeeDuplicate extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	 public static String defaultMessage = "用户名错误:{}, {}已有值重复.";

	 ExceptionEmployeeDuplicate(String name, String fieldName) {
		super(defaultMessage, name, fieldName);
	}
}
