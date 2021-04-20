package com.x.processplatform.assemble.surface.jaxrs.review;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPersonEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionPersonEmpty() {
		super("没有指定用户.");
	}

}
