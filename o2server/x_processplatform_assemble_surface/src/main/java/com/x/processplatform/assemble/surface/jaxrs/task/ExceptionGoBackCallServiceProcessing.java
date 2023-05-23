package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionGoBackCallServiceProcessing extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionGoBackCallServiceProcessing(String work) {
		super("go back invoke service processing failure, work:{}.", work);
	}

}
