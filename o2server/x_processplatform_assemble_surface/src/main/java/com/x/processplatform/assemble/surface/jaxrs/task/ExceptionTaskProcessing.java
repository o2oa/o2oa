package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionTaskProcessing extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionTaskProcessing(String id) {
		super("任务 {} 处理失败.", id);
	}

}
