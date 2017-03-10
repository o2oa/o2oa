package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.exception.PromptException;

class InvalidOperationException extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	 InvalidOperationException() {
		super("无效的操作.");
	}
}
