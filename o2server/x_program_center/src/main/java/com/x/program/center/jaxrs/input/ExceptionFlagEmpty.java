package com.x.program.center.jaxrs.input;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionFlagEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionFlagEmpty() {
		super("标识为空.");
	}
}
