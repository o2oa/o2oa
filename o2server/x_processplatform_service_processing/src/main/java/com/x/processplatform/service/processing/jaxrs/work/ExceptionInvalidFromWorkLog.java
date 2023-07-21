package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidFromWorkLog extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionInvalidFromWorkLog(String work) {
		super("无法找到指定的出发工作日志, 工作:{}.");
	}
}
