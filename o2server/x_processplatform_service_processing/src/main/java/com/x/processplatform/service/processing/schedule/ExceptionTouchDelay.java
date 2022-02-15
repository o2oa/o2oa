package com.x.processplatform.service.processing.schedule;

import com.x.base.core.project.exception.PromptException;

class ExceptionTouchDelay extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionTouchDelay(Exception e, String id, String title, String sequence) {
		super(e, "延时工作触发失败, id:{}, title:{}, sequence:{}.", id, title, sequence);
	}

}
