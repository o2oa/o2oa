package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNoneIdentity extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionNoneIdentity(String person) {
		super("指定用户没有找到身份: {}.", person);
	}
}
