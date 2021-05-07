package com.x.cms.assemble.control.jaxrs.templateform;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidName extends LanguagePromptException {

	private static final long serialVersionUID = 6984800093761853101L;

	ExceptionInvalidName(String name) {
		super("名称校验不通过:{}.", name);
	}

}
