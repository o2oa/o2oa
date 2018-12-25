package com.x.cms.assemble.control.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionCategoryInfoNotFound extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionCategoryInfoNotFound(String name, String id, String alias) {
		super("process name{}, id:{}, alias:{} not found.", name, id, alias);
	}
}
