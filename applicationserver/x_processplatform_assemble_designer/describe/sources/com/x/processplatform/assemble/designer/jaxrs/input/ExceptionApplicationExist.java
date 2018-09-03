package com.x.processplatform.assemble.designer.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionApplicationExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionApplicationExist(String flag) {
		super("应用: {} 已存在.", flag);
	}
}
