package com.x.organization.assemble.personal.jaxrs.person;


import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidEmployee extends LanguagePromptException {

	private static final long serialVersionUID = 4622760821556680073L;

	public static String defaultMessage = "员工号不能为空,且不能使用特殊字符:{}.";

	ExceptionInvalidEmployee(String unique) {
		super(defaultMessage, unique);
	}
}
