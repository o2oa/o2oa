package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionSplittingCannotCallback extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionSplittingCannotCallback(String workId, String workLogId) {
		super("工作无法回滚到处于拆分的状态,work:{}, workLog:{}.", workId, workLogId);
	}
}
