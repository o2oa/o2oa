package com.x.cms.assemble.control.jaxrs.output;

import com.x.base.core.project.exception.PromptException;

class ExceptionCategoryInfoNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionCategoryInfoNotExist(String flag) {
		super("process: {} not existed.", flag);
	}
}
