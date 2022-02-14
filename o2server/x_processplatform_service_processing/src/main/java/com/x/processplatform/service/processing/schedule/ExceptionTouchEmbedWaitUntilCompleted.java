package com.x.processplatform.service.processing.schedule;

import com.x.base.core.project.exception.PromptException;

class ExceptionTouchEmbedWaitUntilCompleted extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionTouchEmbedWaitUntilCompleted(Exception e, String id, String job) {
		super(e, "子流程结束工作触发失败, id:{}, job:{}.", id, job);
	}

}
