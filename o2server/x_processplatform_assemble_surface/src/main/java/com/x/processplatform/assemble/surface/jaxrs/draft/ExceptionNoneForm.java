package com.x.processplatform.assemble.surface.jaxrs.draft;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNoneForm extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionNoneForm() {
		super("无法找到表单.");
	}
}
