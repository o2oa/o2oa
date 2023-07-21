package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionGoBackWay extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionGoBackWay(String way) {
		super("can not find go back way:{}.", way);
	}

}
