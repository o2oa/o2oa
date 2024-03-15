package com.x.processplatform.service.processing.jaxrs.record;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkCompletedNotFoundWorkLog extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionWorkCompletedNotFoundWorkLog(String workCompletedId) {
		super("已完成工作无法定位workLog,id: {}.", workCompletedId);
	}

}
