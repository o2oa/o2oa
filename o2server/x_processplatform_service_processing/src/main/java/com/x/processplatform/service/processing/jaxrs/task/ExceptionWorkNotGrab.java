package com.x.processplatform.service.processing.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkNotGrab extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionWorkNotGrab(String flag) {
		super("工作: {} 没有处于人工活动的抢办状态.", flag);
	}
}
