package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionGoBackWorkLog extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionGoBackWorkLog(String activity) {
		super("can not find go back activity:{}.", activity);
	}

}
