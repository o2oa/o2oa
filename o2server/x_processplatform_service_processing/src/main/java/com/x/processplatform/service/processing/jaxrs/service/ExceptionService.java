package com.x.processplatform.service.processing.jaxrs.service;

import com.x.base.core.project.exception.PromptException;

class ExceptionService extends PromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionService(String workId) {
		super("服务执行失败,工作:{}.", workId);
	}
}
