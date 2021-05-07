package com.x.organization.assemble.control.jaxrs.group;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNameEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionNameEmpty() {
		super("群组名称不能为空.");
	}
}
