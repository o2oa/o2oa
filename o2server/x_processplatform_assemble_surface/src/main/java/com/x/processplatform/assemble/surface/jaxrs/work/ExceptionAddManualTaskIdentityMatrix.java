package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAddManualTaskIdentityMatrix extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionAddManualTaskIdentityMatrix(String workId) {
		super("添加待办身份错误, work:{}.", workId);
	}
}
