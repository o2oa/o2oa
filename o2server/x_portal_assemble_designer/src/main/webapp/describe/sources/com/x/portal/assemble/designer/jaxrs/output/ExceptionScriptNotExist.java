package com.x.portal.assemble.designer.jaxrs.output;

import com.x.base.core.project.exception.PromptException;

class ExceptionScriptNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionScriptNotExist(String flag) {
		super("资源: {} 不存在.", flag);
	}
}
