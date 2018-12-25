package com.x.cms.assemble.control.jaxrs.output;

import com.x.base.core.project.exception.PromptException;

class ExceptionFlagNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionFlagNotExist(String flag) {
		super("flag: {} not existed.", flag);
	}
}
