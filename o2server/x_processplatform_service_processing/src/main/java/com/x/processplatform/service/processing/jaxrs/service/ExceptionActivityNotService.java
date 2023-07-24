package com.x.processplatform.service.processing.jaxrs.service;

import com.x.base.core.project.exception.PromptException;

class ExceptionActivityNotService extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionActivityNotService(String id) {
		super("工作:{}, 未处于服务活动环节.", id);
	}

}
