package com.x.processplatform.service.processing.jaxrs.record;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkOrWorkCompletedNotExist extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionWorkOrWorkCompletedNotExist(String job) {
		super("工作或者已完成工作不存在,job: {}.", job);
	}

}
