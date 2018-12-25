package com.x.processplatform.service.processing.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidCount extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionInvalidCount(Integer count) {
		super("返回数量不正确: {}.", count);
	}
}
