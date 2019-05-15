package com.x.organization.assemble.personal.jaxrs.trust;

import com.x.base.core.project.exception.PromptException;

class ExceptionWholeExist extends PromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionWholeExist(String identity) {
		super("身份 {} 的全局设置已经存在.", identity);
	}

}
