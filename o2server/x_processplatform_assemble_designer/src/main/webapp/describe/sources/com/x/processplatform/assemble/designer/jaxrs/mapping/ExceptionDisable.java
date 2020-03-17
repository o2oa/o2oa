package com.x.processplatform.assemble.designer.jaxrs.mapping;

import com.x.base.core.project.exception.PromptException;

class ExceptionDisable extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionDisable(String name) {
		super("{} 没有启用.", name);
	}

}
