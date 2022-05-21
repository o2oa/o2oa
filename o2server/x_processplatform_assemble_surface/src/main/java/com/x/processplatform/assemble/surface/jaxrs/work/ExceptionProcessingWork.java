package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionProcessingWork extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionProcessingWork(String workId) {
		super("流转工作错误, work:{}.", workId);
	}
}
