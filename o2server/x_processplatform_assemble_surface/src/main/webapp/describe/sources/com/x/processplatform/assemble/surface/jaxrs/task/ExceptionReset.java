package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class ExceptionReset extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionReset(String id) {
		super("任务: {} 重置处理人失败.", id);
	}

}
