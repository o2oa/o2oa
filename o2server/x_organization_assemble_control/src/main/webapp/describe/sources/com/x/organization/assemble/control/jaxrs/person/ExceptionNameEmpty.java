package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.PromptException;

class ExceptionNameEmpty extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionNameEmpty() {
		super("个人名称不能为空.");
	}
}
