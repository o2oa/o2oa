package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyOption extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionEmptyOption() {
		super("otpion empty.");
	}

}
