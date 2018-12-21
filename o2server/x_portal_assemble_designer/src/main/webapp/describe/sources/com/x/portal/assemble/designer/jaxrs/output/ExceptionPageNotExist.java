package com.x.portal.assemble.designer.jaxrs.output;

import com.x.base.core.project.exception.PromptException;

class ExceptionPageNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionPageNotExist(String flag) {
		super("页面: {} 不存在.", flag);
	}
}
