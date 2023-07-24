package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionActivityNotExist extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionActivityNotExist(String activityId) {
		super("活动节点不存在:{}.", activityId);
	}

}
