package com.x.cms.assemble.control.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionFileIncorrect extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionFileIncorrect(String name) {
		super("name: {}, 文件错误.", name);
	}
}
