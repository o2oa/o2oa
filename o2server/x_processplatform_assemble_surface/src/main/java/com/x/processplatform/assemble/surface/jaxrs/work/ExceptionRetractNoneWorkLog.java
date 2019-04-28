package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionRetractNoneWorkLog extends PromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionRetractNoneWorkLog(String workId) {
		super("无法定位召回,工作:{}.", workId);
	}
}
