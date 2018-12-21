package com.x.processplatform.service.processing.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class ExceptionTaskNotExist extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionTaskNotExist(String flag) {
		super("待办: {} 不存在.", flag);
	}
}
