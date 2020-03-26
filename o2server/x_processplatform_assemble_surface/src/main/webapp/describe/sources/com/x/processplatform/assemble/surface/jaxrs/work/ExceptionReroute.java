package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionReroute extends PromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionReroute(String workId) {
		super("调度失败,工作:{}.", workId);
	}
}
