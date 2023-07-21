package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionRecord extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionRecord(String work) {
		super("工作: {} 记录错误.", work);
	}
}
