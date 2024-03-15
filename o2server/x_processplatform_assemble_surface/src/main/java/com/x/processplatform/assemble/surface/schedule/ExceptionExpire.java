package com.x.processplatform.assemble.surface.schedule;

import com.x.base.core.project.exception.PromptException;

class ExceptionExpire extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionExpire(Exception e, String id, String title, String sequence) {
		super(e, "标识过期待办失败, id:{}, title:{}, sequence:{}.");
	}

}
