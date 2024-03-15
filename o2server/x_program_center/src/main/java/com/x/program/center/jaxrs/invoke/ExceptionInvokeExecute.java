package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvokeExecute extends LanguagePromptException {

	private static final long serialVersionUID = -8597019540568284908L;

	ExceptionInvokeExecute(Throwable cause, String id, String name) {
		super(cause, "invoke execute error, id:{}, name:{}, message:{}.", id, name, cause.getMessage());
	}
}