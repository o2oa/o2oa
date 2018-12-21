package com.x.cms.assemble.control.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionScriptNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionScriptNotExist(String flag) {
		super("script: {} not existed.", flag);
	}
}
