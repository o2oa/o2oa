package com.x.organization.assemble.authentication.jaxrs.welink;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonNotExist extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionPersonNotExist(String name) {
		super("person:{} not exist.", name);
	}
}
