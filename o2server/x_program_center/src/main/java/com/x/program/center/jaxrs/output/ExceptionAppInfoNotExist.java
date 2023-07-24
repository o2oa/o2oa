package com.x.program.center.jaxrs.output;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppInfoNotExist extends PromptException {

	private static final long serialVersionUID = -4620752705942326196L;

	ExceptionAppInfoNotExist(String flag) {
		super("应用信息:{} 不存在.", flag);
	}
}
