package com.x.processplatform.assemble.surface.jaxrs.draft;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNoneIdentity extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionNoneIdentity(String person) {
		super("无法识别用户身份: {}.", person);
	}
}
