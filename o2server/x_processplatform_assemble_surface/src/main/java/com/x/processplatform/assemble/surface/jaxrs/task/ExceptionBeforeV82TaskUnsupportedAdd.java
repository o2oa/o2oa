package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionBeforeV82TaskUnsupportedAdd extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionBeforeV82TaskUnsupportedAdd(String id) {
		super("V8.2版本以前待办不支持加签操作,请调度后重新操作: {}.", id);
	}

}
