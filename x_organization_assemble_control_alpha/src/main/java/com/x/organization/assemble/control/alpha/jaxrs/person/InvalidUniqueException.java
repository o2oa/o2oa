package com.x.organization.assemble.control.alpha.jaxrs.person;

import java.util.Objects;

import com.x.base.core.exception.PromptException;

 class InvalidUniqueException extends PromptException {

	private static final long serialVersionUID = 4622760821556680073L;
 InvalidUniqueException(String unique) {
		super("员工唯一标志错误,不能使用特殊字符:" + Objects.toString(unique) + ".");
	}
}
