package com.x.cms.assemble.control.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppInfoNotFound extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionAppInfoNotFound(String name, String id, String alias) {
		super("application name{}, id:{}, alias:{} not found.", name, id, alias);
	}
}
