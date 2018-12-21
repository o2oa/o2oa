package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidOperation extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	 ExceptionInvalidOperation() {
		super("无效的操作.");
	}
}
