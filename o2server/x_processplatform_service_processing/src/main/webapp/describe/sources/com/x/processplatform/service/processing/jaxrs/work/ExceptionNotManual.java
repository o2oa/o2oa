package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionNotManual extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionNotManual(String activityId) {
		super("非人工节点:{}.", activityId);
	}

}
