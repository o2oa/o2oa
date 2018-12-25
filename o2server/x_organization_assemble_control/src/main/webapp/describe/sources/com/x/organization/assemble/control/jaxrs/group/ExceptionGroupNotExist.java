package com.x.organization.assemble.control.jaxrs.group;

import com.x.base.core.project.exception.PromptException;

class ExceptionGroupNotExist extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionGroupNotExist(String flag) {
		super("群组: {} 不存在.", flag);
	}
}
