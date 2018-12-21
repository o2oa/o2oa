package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidGenderType extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionInvalidGenderType() {
		super("性别不可为空.");
	}
}
