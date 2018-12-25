package com.x.processplatform.service.processing.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkNotExist extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionWorkNotExist(String flag) {
		super("工作: {} 不存在.", flag);
	}
}
