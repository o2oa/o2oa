package com.x.program.center.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppInfoExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionAppInfoExist(String flag) {
		super("应用: {} 已存在.", flag);
	}
}
