package com.x.organization.assemble.personal.jaxrs.person;

import java.util.Objects;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidEmployee extends PromptException {

	private static final long serialVersionUID = 4622760821556680073L;

	ExceptionInvalidEmployee(String unique) {
		super("员工号不能为空,且不能使用特殊字符:" + Objects.toString(unique) + ".");
	}
}
