package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.exception.PromptException;

class InvalidGenderTypeException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InvalidGenderTypeException() {
		super("性别不可为空.");
	}
}
