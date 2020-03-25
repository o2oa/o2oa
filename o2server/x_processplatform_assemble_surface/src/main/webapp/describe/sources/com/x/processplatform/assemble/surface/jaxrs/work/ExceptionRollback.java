package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionRollback extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionRollback(String work) {
		super("工作: {} 回滚失败.", work);
	}
}
