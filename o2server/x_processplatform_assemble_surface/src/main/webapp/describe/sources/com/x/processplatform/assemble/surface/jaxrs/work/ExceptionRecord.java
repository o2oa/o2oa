package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionRecord extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionRecord(String work) {
		super("工作: {} 记录错误.", work);
	}
}
