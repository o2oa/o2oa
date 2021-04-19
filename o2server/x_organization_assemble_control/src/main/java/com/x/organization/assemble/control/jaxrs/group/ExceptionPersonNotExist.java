package com.x.organization.assemble.control.jaxrs.group;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPersonNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionPersonNotExist(String name) {
		super("个人: {} 不存在.", name);
	}
}
