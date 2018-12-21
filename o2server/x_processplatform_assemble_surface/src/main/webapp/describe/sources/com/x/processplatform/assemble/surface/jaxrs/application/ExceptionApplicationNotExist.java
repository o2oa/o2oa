package com.x.processplatform.assemble.surface.jaxrs.application;

import com.x.base.core.project.exception.PromptException;

class ExceptionApplicationNotExist extends PromptException {

	private static final long serialVersionUID = -4908883340253465376L;

	ExceptionApplicationNotExist(String flag) {
		super("指定的应用不存在:{}.", flag);
	}

}
