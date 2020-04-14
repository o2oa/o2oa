package com.x.processplatform.assemble.surface.jaxrs.draft;

import com.x.base.core.project.exception.PromptException;

class ExceptionNoneForm extends PromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionNoneForm() {
		super("无法找到表单.");
	}
}
