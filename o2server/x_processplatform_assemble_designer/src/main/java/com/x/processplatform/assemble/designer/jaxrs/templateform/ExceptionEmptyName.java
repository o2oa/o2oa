package com.x.processplatform.assemble.designer.jaxrs.templateform;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyName extends PromptException {

	private static final long serialVersionUID = 6984800093761853101L;

	ExceptionEmptyName() {
		super("名称不能为空.");
	}

}
