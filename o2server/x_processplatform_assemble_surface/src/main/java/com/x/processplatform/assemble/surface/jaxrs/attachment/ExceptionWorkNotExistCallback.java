package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionWorkNotExistCallback extends CallbackPromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionWorkNotExistCallback(String callbackName, String workId) {
		super(callbackName, "指定的工作不存在:{}.", workId);
	}

}
