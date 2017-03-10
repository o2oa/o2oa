package com.x.processplatform.assemble.surface.jaxrs.application;

import com.x.base.core.exception.PromptException;

class ApplicationNotExistedException extends PromptException {

	private static final long serialVersionUID = -4908883340253465376L;

	ApplicationNotExistedException(String flag) {
		super("指定的应用不存在:{}.", flag);
	}

}
