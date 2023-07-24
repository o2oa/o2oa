package com.x.server.console.action;

import com.x.base.core.project.exception.PromptException;

class ExceptionMappingNotExist extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionMappingNotExist() {
		super("can not find storageMapping");
	}

}
