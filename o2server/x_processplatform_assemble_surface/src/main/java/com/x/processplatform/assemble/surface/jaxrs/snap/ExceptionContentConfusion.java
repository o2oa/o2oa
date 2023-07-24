package com.x.processplatform.assemble.surface.jaxrs.snap;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionContentConfusion extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionContentConfusion() {
		super("snap content confusion.");
	}
}
