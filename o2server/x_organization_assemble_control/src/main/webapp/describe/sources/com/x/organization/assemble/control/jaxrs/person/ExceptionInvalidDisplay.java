package com.x.organization.assemble.control.jaxrs.person;

import java.util.Objects;

import com.x.base.core.project.exception.PromptException;

 class ExceptionInvalidDisplay extends PromptException {

	private static final long serialVersionUID = 4622760821556680073L;

	 ExceptionInvalidDisplay(String unique) {
		super("显示名错误,不能使用特殊字符:" + Objects.toString(unique) + ".");
	}
}
