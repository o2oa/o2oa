package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionReset extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionReset(String id) {
		super("任务: {} 重置处理人失败.", id);
	}

}
