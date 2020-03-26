package com.x.query.assemble.designer.jaxrs.reveal;

import com.x.base.core.project.exception.PromptException;

class ExceptionQueryNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionQueryNotExist(String flag) {
		super("查询: {} 不存在.", flag);
	}
}
