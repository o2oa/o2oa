package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidWorkLog extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionInvalidWorkLog(String activityToken,Integer count) {
		super("无效工作日志,activityToken:{}, count:{}.", activityToken,count);
	}
}
