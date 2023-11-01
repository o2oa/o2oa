package com.x.processplatform.assemble.surface.schedule;

import com.x.base.core.project.exception.PromptException;

class ExceptionTouchDetained extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionTouchDetained(Exception e, String id, String title, String sequence) {
		super(e, "停滞工作触发失败, id:{}, title:{}, sequence:{}.");
	}

}
