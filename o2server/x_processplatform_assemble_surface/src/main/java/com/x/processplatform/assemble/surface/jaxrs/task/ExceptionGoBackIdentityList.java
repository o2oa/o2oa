package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionGoBackIdentityList extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionGoBackIdentityList() {
		super("go back identity list is empty.");
	}

}
