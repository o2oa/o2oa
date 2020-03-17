package com.x.processplatform.assemble.designer.jaxrs.process;

import com.x.base.core.project.exception.PromptException;

class ExceptionAlreadyAddQueue extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionAlreadyAddQueue() {
		super("任务已加入队列.");
	}

}
