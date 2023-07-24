package com.x.processplatform.service.processing.schedule;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvokeProcessingTask extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionInvokeProcessingTask(String id) {
		super("调用待办流转失败, id:{}.", id);
	}

}
