package com.x.query.assemble.designer.jaxrs.output;

import com.x.base.core.project.exception.PromptException;

class ExceptionViewNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionViewNotExist(String flag) {
		super("视图: {} 不存在.", flag);
	}
}
