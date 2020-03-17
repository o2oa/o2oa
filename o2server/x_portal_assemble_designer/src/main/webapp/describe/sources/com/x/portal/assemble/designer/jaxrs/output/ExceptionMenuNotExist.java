package com.x.portal.assemble.designer.jaxrs.output;

import com.x.base.core.project.exception.PromptException;

class ExceptionMenuNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionMenuNotExist(String flag) {
		super("菜单: {} 不存在.", flag);
	}
}
