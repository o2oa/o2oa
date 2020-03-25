package com.x.processplatform.service.processing.schedule;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvokeProcessingWork extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionInvokeProcessingWork(String id) {
		super("调用工作流转失败, id:{}.", id);
	}

}
