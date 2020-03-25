package com.x.processplatform.service.processing.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkNotAtManual extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionWorkNotAtManual(String flag) {
		super("工作: {} 没有处于人工活动.", flag);
	}
}
