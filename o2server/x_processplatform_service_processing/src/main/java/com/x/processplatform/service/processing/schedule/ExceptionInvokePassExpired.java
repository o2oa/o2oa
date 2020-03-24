package com.x.processplatform.service.processing.schedule;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvokePassExpired extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionInvokePassExpired(String id) {
		super("调用待办过期, id:{}.", id);
	}

}
