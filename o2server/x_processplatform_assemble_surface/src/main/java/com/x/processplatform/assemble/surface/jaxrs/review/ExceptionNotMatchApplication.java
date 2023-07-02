package com.x.processplatform.assemble.surface.jaxrs.review;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNotMatchApplication extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionNotMatchApplication(String id, String applicationFlag) {
		super("参阅应用标识: {} 和指定的应用不匹配: {}.", id, applicationFlag);
	}
}
