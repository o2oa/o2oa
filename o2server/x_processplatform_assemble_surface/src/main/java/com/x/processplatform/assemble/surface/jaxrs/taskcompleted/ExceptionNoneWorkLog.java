package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import com.x.base.core.project.exception.PromptException;

class ExceptionNoneWorkLog extends PromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionNoneWorkLog() {
		super("无法找到工作日志.");
	}
}
